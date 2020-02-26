package eu.perfect.core.menu.pagination.slot

import eu.perfect.core.menu.events.MenuPlayerInventory
import eu.perfect.core.menu.events.MenuPlayerSlotRender
import eu.perfect.core.menu.pagination.MenuPaginationImpl
import eu.perfect.core.menu.slot.SlotDSL
import org.bukkit.entity.Player
import java.util.*

class PaginationSlotDSLImpl<T>(
    private val pagination: MenuPaginationImpl<T>,
    override val slotRoot: SlotDSL
) : PaginationSlotDSL<T> {
    override val paginationEventHandler = PaginationSlotEventHandler<T>()

    override var cancel: Boolean
        get() = slotRoot.cancel
        set(value) { slotRoot.cancel = value }

    override val slotData: WeakHashMap<String, Any>
        get() = slotRoot.slotData
    override val playerSlotData: WeakHashMap<Player, WeakHashMap<String, Any>>
        get() = slotRoot.playerSlotData

    internal fun updateSlot(
        actualItem: T?,
        nextItem: T?,
        slotPos: Int,
        menuPlayerInventory: MenuPlayerInventory,
        isPageChange: Boolean = false
    ) {
        if(isPageChange) {
            relocateSlotData(actualItem, nextItem)

            // triggering event
            paginationEventHandler.handlePageChange(
                    actualItem,
                    MenuPlayerSlotPageChange(
                            pagination.menu,
                            slotPos,
                            slotRoot,
                            menuPlayerInventory.player,
                            menuPlayerInventory.inventory
                    )
            )
        }

        // cleaning item in the inventory slot
        menuPlayerInventory.setItem(slotPos, null)

        paginationEventHandler.handleRender(
                nextItem,
            MenuPlayerSlotRender(
                pagination.menu,
                slotPos,
                slotRoot,
                menuPlayerInventory.player,
                menuPlayerInventory.inventory
            )
        )
    }

    internal fun relocateSlotData(actualItem: T?, nextItem: T?) {
        if(actualItem != null) {
            // caching the current Data from Slot
            val slotData = WeakHashMap(slotData)
            val playerSlotData = WeakHashMap(playerSlotData)

            if(slotData.isNotEmpty())
                pagination.itemSlotData[actualItem] = slotData

            if(playerSlotData.isNotEmpty())
                pagination.itemPlayerSlotData[actualItem] = playerSlotData
        }

        // cleaning current data in the Slot
        slotData.clear()
        playerSlotData.clear()

        if(nextItem != null) {
            val nextSlotData = pagination.itemSlotData[nextItem]
            val nextPlayerSlotData = pagination.itemPlayerSlotData[nextItem]

            if(nextSlotData != null)
                slotData.putAll(nextSlotData)
            if(nextPlayerSlotData != null)
                playerSlotData.putAll(nextPlayerSlotData)
        }
    }
}