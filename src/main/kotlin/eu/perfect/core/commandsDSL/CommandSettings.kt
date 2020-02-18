package eu.perfect.core.commandsDSL

data class CommandSettings(
    var name: String,
    var aliases: List<String> = listOf(),
    var description: String? = null,
    var usage: String? = null,
    var permission: String? = null,
    var permissionMessage: String? = null,
    var notExecutorTypeMessage: String? = null
)