package cc.pe3epwithyou.trident.utils

import java.util.concurrent.ConcurrentHashMap

/**
 * A thread-safe map for managing cooldowns associated with specific keys.
 *
 * This map stores the cooldown time (in milliseconds as a [Long]) for each key of [Any] type,
 * which can be used to track the time remaining before an action associated with the key can be performed again.
 */
val cooldowns = ConcurrentHashMap<Any, Long>()

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