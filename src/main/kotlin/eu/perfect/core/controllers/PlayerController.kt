package eu.perfect.core.controllers

import eu.perfect.core.PerfectCore
import eu.perfect.core.providePerfectCore
import eu.perfect.core.utils.*
import eu.perfect.core.utils.collections.onlinePlayerMapOf
import eu.perfect.core.utils.extensions.KListener
import eu.perfect.core.utils.extensions.displaced
import eu.perfect.core.utils.extensions.event
import eu.perfect.core.utils.extensions.scheduler
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.server.PluginDisableEvent

internal fun providePlayerController() = providePerfectCore().playerController

internal class PlayerController(
        override val plugin: PerfectCore
) : KListener<PerfectCore>, PerfectController {

    internal val inputCallbacks by lazy { plugin.onlinePlayerMapOf<ChatInput>() }
    internal val functionsMove by lazy { plugin.onlinePlayerMapOf<PlayerCallback<Boolean>>() }
    internal val functionsQuit by lazy { plugin.onlinePlayerMapOf<PlayerCallback<Unit>>() }

    override fun onEnable() {
        event<AsyncPlayerChatEvent>(ignoreCancelled = true) {
            if (message.isNotBlank()) {
                val input = inputCallbacks.remove(player)
                if (input != null) {
                    if (input.sync) scheduler {
                        input.callback(
                            player,
                            message
                        )
                    }.runTask(plugin)
                    else input.callback(player, message)
                    isCancelled = true
                }
            }
        }
        event<PlayerMoveEvent>(ignoreCancelled = true) {
            if (displaced) {
                if (functionsMove[player]?.run { callback.invoke(player) } == true) {
                    isCancelled = true
                }
            }
        }
        event<PluginDisableEvent> {
            inputCallbacks.entries.filter { it.value.plugin == plugin }.forEach {
                inputCallbacks.remove(it.key)
            }
            functionsMove.entries.filter { it.value.plugin == plugin }.forEach {
                functionsMove.remove(it.key)
            }
            functionsQuit.entries.filter { it.value.plugin == plugin }.forEach {
                functionsQuit.remove(it.key)
            }
        }
    }
}