package cc.pe3epwithyou.trident.dialogs

import cc.pe3epwithyou.trident.utils.ChatUtils
import com.noxcrew.sheeplib.dialog.Dialog

abstract class TridentDialog(x: Int, y: Int, private val key: String) : Dialog(x, y) {
    open fun refresh() {
        super.init()
    }

    override fun onClose() {
        ChatUtils.info("onClose dialog $key has been triggered.")
        ChatUtils.info("$key position: ${this.x} ${this.y}")
        DialogCollection.remove(key)
        DialogCollection.saveDialogPosition(key, Pair(this.x, this.y))
        super.onClose()
    }
}