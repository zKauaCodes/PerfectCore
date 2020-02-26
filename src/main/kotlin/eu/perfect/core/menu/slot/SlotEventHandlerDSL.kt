package eu.perfect.core.menu.slot

import eu.perfect.core.menu.events.MenuPlayerSlotInteract
import eu.perfect.core.menu.events.MenuPlayerSlotMoveTo
import eu.perfect.core.menu.events.MenuPlayerSlotRender
import eu.perfect.core.menu.events.MenuPlayerSlotUpdate

typealias MenuPlayerSlotInteractEvent = MenuPlayerSlotInteract.() -> Unit
typealias MenuPlayerSlotRenderEvent = MenuPlayerSlotRender.() -> Unit
typealias MenuPlayerSlotUpdateEvent = MenuPlayerSlotUpdate.() -> Unit
typealias MenuPlayerSlotMoveToEvent = MenuPlayerSlotMoveTo.() -> Unit

class SlotEventHandlerDSL : SlotEventHandler {

    val interactCallbacks = mutableListOf<MenuPlayerSlotInteractEvent>()
    val renderCallbacks = mutableListOf<MenuPlayerSlotRenderEvent>()
    val updateCallbacks = mutableListOf<MenuPlayerSlotUpdateEvent>()
    val moveToSlotCallbacks = mutableListOf<MenuPlayerSlotMoveToEvent>()

    override fun interact(interact: MenuPlayerSlotInteract) {
        for (callback in interactCallbacks) {
            callback(interact)
        }
    }

    override fun render(render: MenuPlayerSlotRender) {
        for (callback in renderCallbacks) {
            callback(render)
        }
    }

    override fun update(update: MenuPlayerSlotUpdate) {
        for (callback in updateCallbacks) {
            callback(update)
        }
    }

    override fun moveToSlot(moveToSlot: MenuPlayerSlotMoveTo) {
        for (callback in moveToSlotCallbacks) {
            callback(moveToSlot)
        }
    }

    override fun clone(): SlotEventHandlerDSL {
        return SlotEventHandlerDSL().also {
            it.interactCallbacks.addAll(interactCallbacks)
            it.renderCallbacks.addAll(renderCallbacks)
            it.updateCallbacks.addAll(updateCallbacks)
            it.moveToSlotCallbacks.addAll(moveToSlotCallbacks)
        }
    }

}