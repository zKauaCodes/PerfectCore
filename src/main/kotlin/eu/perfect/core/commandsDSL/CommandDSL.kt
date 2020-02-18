package eu.perfect.core.commandsDSL

import eu.perfect.core.PerfectCore
import eu.perfect.core.utils.BukkitReflection
import eu.perfect.core.utils.WithPlugin
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

inline fun Plugin.settings(
    name: String,
    aliases: List<String> = listOf(),
    description: String? = null,
    usage: String? = null,
    permission: String? = null,
    permissionMessage: String? = null,
    invalidTypeMessage: String? = null
) = CommandSettings(name, aliases, description, usage, permission, permissionMessage, invalidTypeMessage)

inline fun <reified T : CommandSender> Plugin.command(
    commandSettings: CommandSettings,
    server: Server = Bukkit.getServer(),
    crossinline block: Executor<T>.() -> Unit
): CommandSettings {

    val map = BukkitReflection.commandMap(server)
    val command = object : BukkitCommand(commandSettings.name) {

        init {
            if (commandSettings.aliases.isNotEmpty()) {
                aliases = commandSettings.aliases
            }
            commandSettings.description?.let { description = it }
            commandSettings.usage?.let { usage = it }
        }

        override fun execute(p0: CommandSender, p1: String, p2: Array<out String>): Boolean {
            if (p0 !is T) {
                commandSettings.notExecutorTypeMessage?.let {
                    p0.sendMessage(it)
                }
                return false
            }
            commandSettings.permission?.let {
                // TODO verificar a permissão pelo sistema proprio
                if (!p0.hasPermission(it)) {
                    p0.sendMessage(commandSettings.permissionMessage)
                    return false
                }
            }
            val executor = Executor<T>(
                p0 as T,
                p2.toList(),
                commandSettings
            )
            block(executor)
            return false
        }

    }
    map.register(commandSettings.name, command)
    // TODO adicionar as mensagem comando Staff registrado para a classe StaffCommand
    return commandSettings
}

inline fun <reified T : CommandSender> Plugin.command(
    name: String,
    aliases: List<String> = listOf(),
    description: String? = null,
    usage: String? = null,
    invalidTypeMessage: String? = null,
    server: Server = Bukkit.getServer(),
    crossinline block: Executor<T>.() -> Unit
) = command<T>(
    settings(
        name = name,
        aliases = aliases,
        description = description,
        usage = usage,
        invalidTypeMessage = invalidTypeMessage
    ), server, block
)

inline operator fun CommandSettings.compareTo(
    crossinline block: Executor<Player>.() -> Unit
): Int {

    PerfectCore.instance.command<Player>(this, Bukkit.getServer(), block)
    return 0
}

inline operator fun CommandSettings.rem(
    crossinline block: Executor<CommandSender>.() -> Unit
) = PerfectCore.instance.command<CommandSender>(this, Bukkit.getServer(), block)

inline operator fun CommandSettings.plus(
    crossinline block: Executor<ConsoleCommandSender>.() -> Unit
) = PerfectCore.instance.command<ConsoleCommandSender>(this, Bukkit.getServer(), block)