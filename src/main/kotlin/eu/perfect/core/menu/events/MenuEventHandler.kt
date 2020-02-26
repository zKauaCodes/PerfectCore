package eu.perfect.core.menu.events

import eu.perfect.core.menu.events.*

interface MenuEventHandler {

    fun update(update: MenuPlayerUpdate)

    fun close(close: MenuPlayerClose)

    fun moveToMenu(moveToMenu: MenuPlayerMoveTo)

    fun preOpen(preOpen: MenuPlayerPreOpen)

    fun open(open: MenuPlayerOpen)
}