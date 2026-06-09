package cc.pe3epwithyou.trident.feature.chat

interface ChatController {
    /**
     * Checks if the chat message should be modified
     *
     * @param original Original chat message sent by user before being modified
     * @return `true` if the message should be processed
     */
    fun shouldModifyChat(original: String): Boolean = false

    /**
     * Checks if the command should be modified
     *
     * @param original Original command sent by user before being modified
     * @return `true` if the message should be processed
     */
    fun shouldModifyCommand(original: String): Boolean = false

    /**
     * Processes the original chat message and returns the command string.
     * Command string is returned without `/`
     *
     * @param original Original chat message sent by user before being modified
     * @return Modified command to be sent to the server
     */
    fun processChat(original: String): String? = null

    /**
     * Processes the original command and returns the modified command string.
     * Command string is returned without `/`
     *
     * @param original Original command sent by user before being modified
     * @return Modified command to be sent to the server
     */
    fun processCommand(original: String): String? = null
}