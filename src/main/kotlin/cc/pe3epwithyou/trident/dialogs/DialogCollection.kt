package cc.pe3epwithyou.trident.dialogs

import com.noxcrew.sheeplib.DialogContainer

/**
 * Collection that manages a collection of opened dialogs identified by unique keys.
 *
 * This object maintains a map of currently opened dialogs and provides methods to open,
 * close, refresh, and clear dialogs.
 */
object DialogCollection {
    private val openedDialogs = hashMapOf<String, TridentDialog>()

    /**
     * Opens a dialog with the specified [key] if it is not already opened.
     *
     * @param key Unique identifier for the dialog.
     * @param dialog The [TridentDialog] instance to open.
     */
    fun open(key: String, dialog: TridentDialog) {
        if (openedDialogs.containsKey(key)) return

        openedDialogs.putIfAbsent(key, dialog)
        DialogContainer += dialog
    }

    /**
     * Closes the dialog associated with the specified [key] if it is currently opened.
     *
     * @param key Unique identifier for the dialog to close.
     */
    fun close(key: String) {
        if (!openedDialogs.containsKey(key)) return

        openedDialogs[key]?.close()
        openedDialogs.remove(key)
    }

    /**
     * Refreshes the dialog layout associated with the specified [key] if it is currently opened.
     *
     * @param key Unique identifier for the dialog to refresh.
     */
    fun refreshDialog(key: String) {
        if (!openedDialogs.containsKey(key)) return
        openedDialogs[key]?.refresh()
    }

    /**
     * Closes all opened dialogs and clears the internal collection.
     */
    fun clear() {
        openedDialogs.forEach { (key, _) ->
            close(key)
        }
    }
}