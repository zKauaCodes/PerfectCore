package eu.perfect.core.menu.slot

import eu.perfect.core.menu.events.MenuPlayerSlotInteract
import eu.perfect.core.menu.events.MenuPlayerSlotMoveTo
import eu.perfect.core.menu.events.MenuPlayerSlotRender
import eu.perfect.core.menu.events.MenuPlayerSlotUpdate

interface SlotEventHandler {

    fun interact(interact: MenuPlayerSlotInteract)

    fun render(render: MenuPlayerSlotRender)

    fun update(update: MenuPlayerSlotUpdate)

    fun moveToSlot(moveToSlot: MenuPlayerSlotMoveTo)

    fun clone(): SlotEventHandler

}