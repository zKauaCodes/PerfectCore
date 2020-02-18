package eu.perfect.core.utils

import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin

interface WithPlugin<T : Plugin> { val plugin: T }

fun WithPlugin<*>.message(message: String) = Bukkit.getConsoleSender().sendMessage(message)