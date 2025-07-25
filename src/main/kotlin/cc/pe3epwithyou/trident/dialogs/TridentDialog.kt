package cc.pe3epwithyou.trident.dialogs

import com.noxcrew.sheeplib.dialog.Dialog

abstract class TridentDialog(x: Int, y: Int, private val key: String) : Dialog(x, y) {
    open fun refresh() {
        super.init()
    }

    override fun onClose() {
        DialogCollection.remove(key)
        super.onClose()
    }
}