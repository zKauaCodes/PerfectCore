package eu.perfect.core.commandsDSL

import org.bukkit.command.CommandSender
import java.lang.Double.parseDouble
import java.lang.Integer.parseInt

data class Executor<T: CommandSender>(
    val sender: T,
    val args: List<String> = listOf(),
    val command: CommandSettings
) {

    var usedValidVariables: Boolean = false

    inline fun permission(
        permission: String
    ): Boolean? = if(sender.hasPermission(permission)) true else null // TODO fazer a verificação com o sistema de grupo

    //not 0-based
    inline fun string(index: Int): String? = if(args.size < index) null else
        args[if(index < 0) 0 else (index - 1)]

    //not 0-based
    inline fun integer(index: Int): Int? {
        val asText = string(index)
        asText?.let { return parseInt(it) }
        return null
    }

    //not 0-based
    inline fun double(index: Int): Double? {
        val asText = string(index)
        asText?.let { return parseDouble(it) }
        return null
    }

    //not 0-based
    inline fun float(index: Int): Float? = double(index)?.toFloat()

    inline fun <T> fail(exception: CommandException? = null, run: () -> T): T {
        apply {
            val result = run()
            exception?.let { throw it }
            return result
        }
    }

    inline fun <T> argument(name: String, id: Int, run: () -> T): Boolean {
        val arg = string(id)
        return with(arg) {
            if(arg != null) {
                if(arg.toLowerCase() == name.toLowerCase()) {
                    usedValidVariables = true
                    run()
                    true
                } else false
            } else false
        }
    }

    fun reply(vararg message: String) = sender.sendMessage(message)

}

data class CommandException(
    override val message: String = "Invalid arguments."
): Exception()