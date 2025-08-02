package cc.pe3epwithyou.trident.widgets.killfeed

import net.minecraft.resources.ResourceLocation

enum class KillType(val transitionSprite: ResourceLocation) {
    SELF_ENEMY(
        ResourceLocation.fromNamespaceAndPath("trident", "textures/interface/killfeed/self_enemy.png")
    ),
    TEAM_ENEMY(
        ResourceLocation.fromNamespaceAndPath("trident", "textures/interface/killfeed/team_enemy.png")
    ),
    ENEMY_SELF(
        ResourceLocation.fromNamespaceAndPath("trident", "textures/interface/killfeed/enemy_self.png")
    ),
    ENEMY_TEAM(
        ResourceLocation.fromNamespaceAndPath("trident", "textures/interface/killfeed/enemy_team.png")
    ),
}