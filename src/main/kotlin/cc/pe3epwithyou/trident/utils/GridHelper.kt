package cc.pe3epwithyou.trident.utils

import com.noxcrew.sheeplib.layout.GridLayoutBuilder
import net.minecraft.client.gui.layouts.GridLayout

inline fun gridLayout(spacing: Int, x: Int = 0, y: Int = 0, builder: GridLayoutBuilder.() -> Unit): GridLayout {
    return GridLayoutBuilder(x, y, spacing).also(builder).build()
}