package cc.pe3epwithyou.trident.dialogs

import com.noxcrew.sheeplib.dialog.Dialog

abstract class TridentDialog(x: Int, y: Int) : Dialog(x, y) {
    open fun refresh() {
        super.init()
    }
}