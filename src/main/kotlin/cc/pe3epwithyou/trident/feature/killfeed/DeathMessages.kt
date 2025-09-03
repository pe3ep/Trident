package cc.pe3epwithyou.trident.feature.killfeed

enum class DeathMessages(
    val regex: Regex, val method: KillMethod
) {
    SLAIN(
        Regex("^\\[.] .+ (was slain by) .+"), KillMethod.MELEE
    ),
    SHOT(
        Regex("^\\[.] .+ (was shot by) .+"), KillMethod.RANGE
    ),
    EXPLODED_KILL(
        Regex("^\\[.] .+ (was blown up by) .+"), KillMethod.EXPLOSION
    ),
    EXPLODED_SELF(
        Regex("^\\[.] .+ blew up.+"), KillMethod.EXPLOSION
    ),
    LAVA_KILL(
        Regex("^\\[.] .+ (tried to swim in lava to escape) .+"), KillMethod.LAVA
    ),
    LAVA_SELF(
        Regex("^\\[.] .+ tried to swim in lava.+"), KillMethod.LAVA
    ),
    MAGIC_KILL(
        Regex("^\\[.] .+ was eliminated with magic by .+"), KillMethod.MAGIC
    ),
    MAGIC_KILL_CONTACT(
        Regex("^\\[.] .+ was hit by .+"), KillMethod.MAGIC
    ),
    LOGGED_OUT_KILL(
        Regex("^\\[.] .+ logged out to get away from .+"), KillMethod.MELEE
    ),
    LOGGED_OUT(
        Regex("^\\[.] .+ logged out.+"), KillMethod.DISCONNECT
    ),
    GENERIC_KILL(
        Regex("^\\[.] .+ was eliminated by .+"), KillMethod.MELEE
    ),
    GENERIC_SELF(
        Regex("^\\[.] .+ died.+"), KillMethod.GENERIC
    ),
    SPLEEFED_KILL(
        Regex("^\\[.] .+ was spleefed by .+"), KillMethod.MELEE
    ),
    PRICKED_KILL(
        Regex("^\\[.] .+ was pricked to death whilst trying to escape .+"), KillMethod.MELEE
    ),
    PRICKED_SELF(
        Regex("^\\[.] .+ was pricked to death.+"), KillMethod.GENERIC
    ),
    FIRE_KILL(
        Regex("^\\[.] .+ walked into fire whilist fighting .+"), KillMethod.MELEE
    ),
    FIRE_SELF(
        Regex("^\\[.] .+ went up in flames.+"), KillMethod.FIRE
    ),
    BURNED_KILL(
        Regex("^\\[.] .+ was burned to a crisp while fighting .+"), KillMethod.FIRE
    ),
    BURNED_SELF(
        Regex("^\\[.] .+ burned to death.+"), KillMethod.FIRE
    ),
    NOT_REJOINED(
        Regex("^\\[.] .+ hasn't rejoined the game and is automatically eliminated.+"), KillMethod.DISCONNECT
    ),
    DISCONNECTED(
        Regex("^\\[.] .+ disconnected.+"), KillMethod.DISCONNECT
    ),
    VOID_KILL(
        Regex("^\\[.] .+ didn't want to live in the same world as .+"), KillMethod.MELEE
    ),
    VOID_SELF(
        Regex("^\\[.] .+ fell out of the world.+"), KillMethod.VOID
    ),
    SUFFOCATE_KILL(
        Regex("^\\[.] .+ suffocated in a wall while fighting .+"), KillMethod.MELEE
    ),
    SUFFOCATE_SELF(
        Regex("^\\[.] .+ suffocated in a wall.+"), KillMethod.GENERIC
    )
}