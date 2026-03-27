package cc.pe3epwithyou.trident.utils

import net.minecraft.nbt.ByteTag
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.FloatTag
import net.minecraft.nbt.IntTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.LongTag
import net.minecraft.nbt.ShortTag
import net.minecraft.nbt.StringTag
import net.minecraft.nbt.Tag

object NbtHelper {
    fun <T> list(list: List<T>): ListTag {
        val nbtList = ListTag()
        nbtList.addAll(list.map { if (it is Tag) it else tag(it) })
        return nbtList
    }

    fun <T> tag(contents: T): Tag  {
        requireNotNull(contents) { "Cannot create tag of null" }
        when (contents) {
            is String -> return StringTag.valueOf(contents)
            is Boolean -> return ByteTag.valueOf(contents)
            is Int -> return IntTag.valueOf(contents)
            is Byte -> return ByteTag.valueOf(contents)
            is Float -> return FloatTag.valueOf(contents)
            is Short -> return ShortTag.valueOf(contents)
            is Long -> return LongTag.valueOf(contents)
            is Double -> return FloatTag.valueOf(contents.toFloat())
            is List<*> -> return list(contents)
        }

        error {
            "Cannot create tag of type ${contents::class.simpleName}"
        }
    }
}

fun <T> CompoundTag.put(key: String, value: T) = put(key, NbtHelper.tag(value))