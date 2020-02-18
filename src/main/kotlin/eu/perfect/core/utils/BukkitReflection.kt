package eu.perfect.core.utils

import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.command.CommandMap
import java.lang.reflect.Field

object BukkitReflection {
    inline fun bukkitVersion(server: Server = Bukkit.getServer()) =
        server.javaClass.`package`.name.split("\\.")[3]

    inline fun nmsClass(
        name: String,
        version: String = bukkitVersion()
    ) = Class.forName("net.minecraft.server.${version}.${name}")

    inline fun craftBukkitClass(
        name: String,
        version: String = bukkitVersion()
    ) = Class.forName("org.bukkit.craftbukkit.${version}.${name}")

    inline fun commandMap(server: Server = Bukkit.getServer()): CommandMap {
        val commandMap: Field = server::class.java.getDeclaredField("commandMap")
        commandMap.isAccessible = true
        return commandMap.get(server) as CommandMap
    }

}
