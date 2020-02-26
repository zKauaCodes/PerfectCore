package eu.perfect.core.controllers

import eu.perfect.core.PerfectCore
import eu.perfect.core.menu.events.MenuPlayerSlotInteract
import eu.perfect.core.menu.getMenuFromInventory
import eu.perfect.core.menu.getMenuFromPlayer
import eu.perfect.core.menu.slotOrBaseSlot
import eu.perfect.core.menu.takeIfHasPlayer
import eu.perfect.core.utils.extensions.KListener
import eu.perfect.core.utils.extensions.onlinePlayers
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerPickupItemEvent
import org.bukkit.event.server.PluginDisableEvent

internal class MenuController(
    override val plugin: PerfectCore
) : KListener<PerfectCore>, PerfectController {

    override fun onEnable() {
        // do nothing
    }

    @EventHandler
    fun pluginDisableEvent(event: PluginDisableEvent) {
        onlinePlayers().forEach {
            val menu = getMenuFromPlayer(it)
                ?.takeIf { it.plugin.name == event.plugin.name }
            menu?.close(it, true)
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun clickInventoryEvent(event: InventoryClickEvent) {
        if (event.view.type == InventoryType.CHEST) {
            val player = event.whoClicked as Player
            val inv = event.inventory

            val menu = getMenuFromInventory(event.inventory)?.takeIfHasPlayer(player)

            if (menu != null) {
                if (event.rawSlot == event.slot) {
                    val clickedSlot = event.slot + 1
                    val slot = menu.slotOrBaseSlot(clickedSlot)

                    val interact = MenuPlayerSlotInteract(
                        menu, clickedSlot, slot,
                        player, inv, slot.cancel,
                        event.click, event.action,
                        event.currentItem, event.cursor,
                        event.hotbarButton
                    )

                    slot.eventHandler.interact(interact)

                    if (interact.canceled) event.isCancelled = true
                } else {
                    // TODO move item
                    event.isCancelled = true
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun clickInteractEvent(event: InventoryDragEvent) {
        if (event.view.type == InventoryType.CHEST) {
            val player = event.whoClicked as Player
            val menu = getMenuFromInventory(event.inventory)?.takeIfHasPlayer(player)
            if (menu != null) {
                val pass = event.inventorySlots.firstOrNull { it in event.rawSlots }
                if (pass != null) event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onClose(event: InventoryCloseEvent) {
        if (event.view.type == InventoryType.CHEST) {
            val player = event.player as Player
            getMenuFromPlayer(player)?.close(player, false)
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onPickupItemEvent(event: PlayerPickupItemEvent) {
        if (getMenuFromPlayer(event.player) != null) event.isCancelled = true
    }
}