package cc.pe3epwithyou.trident.utils

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.milliseconds

/**
 * A thread-safe map for managing cooldowns associated with specific keys.
 *
 * This map stores the cooldown time (in milliseconds as a [Long]) for each key of [Any] type,
 * which can be used to track the time remaining before an action associated with the key can be performed again.
 */
val cooldowns = ConcurrentHashMap<Any, Long>()

private val debounceJobs = HashMap<Any, Job>()
private val debounceLock = Any()

/**
 * Indicates the outcome of an action with a cooldown time.
 * Serves to contain information on whether an action was performed or is currently under a cooldown period.
 * Can be used to execute a block of code if the action was on cooldown.
 *
 * @property executed Indicates whether the action was executed successfully.
 */
@Suppress("unused")
class CooldownResult(
    val executed: Boolean
) {
    inline fun onCooldown(block: () -> Unit): CooldownResult {
        if (isOnCooldown) block()
        return this
    }

    val isOnCooldown: Boolean
        get() = !executed

    val didExecute: Boolean
        get() = executed
}

/**
 * Executes a block of code if the specified reference is not on cooldown and applies a cooldown period to the reference if executed.
 *
 * @param reference an object used to identify the cooldown context
 * @param cooldownMillis the cooldown duration in milliseconds
 * @param block the code block to execute if the cooldown condition is met
 * @return a [CooldownResult] indicating whether the block was executed and if the reference is on cooldown
 */
inline fun withCooldown(
    reference: Any,
    cooldownMillis: Long,
    block: () -> Unit
): CooldownResult {
    val now = System.currentTimeMillis()
    var execute = false

    cooldowns.compute(reference) { _, last ->
        if (last == null || now - last >= cooldownMillis) {
            execute = true
            now
        } else {
            last
        }
    }

    if (execute) block()
    return CooldownResult(execute)
}

/**
 * Debounces a block of code using coroutines.
 *
 * Each call resets the timer for the specified reference.
 * The block is executed only after [debounceMillis] milliseconds
 * have passed without another call.
 *
 * The first execution is also delayed by the full debounce period.
 *
 * @param reference An object used to identify the debounce context.
 * @param debounceMillis The debounce duration in milliseconds.
 * @param block The code block to execute after the debounce period.
 */
fun debounce(
    reference: Any,
    debounceMillis: Long,
    block: () -> Unit
) {
    require(debounceMillis >= 0L) {
        "Debounce duration cannot be negative."
    }

    val job = background().launch(start = CoroutineStart.LAZY) {
        try {
            delay(debounceMillis.milliseconds)

            val shouldExecute = synchronized(debounceLock) {
                if (debounceJobs[reference] === coroutineContext[Job]) {
                    debounceJobs.remove(reference)
                    true
                } else {
                    false
                }
            }

            if (shouldExecute) {
                main {
                    block()
                }
            }
        } finally {
            synchronized(debounceLock) {
                if (debounceJobs[reference] === coroutineContext[Job]) {
                    debounceJobs.remove(reference)
                }
            }
        }
    }

    synchronized(debounceLock) {
        debounceJobs.put(reference, job)?.cancel()
        job.start()
    }
}