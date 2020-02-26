package eu.perfect.core.inventorys

import eu.perfect.core.PerfectCore
import eu.perfect.core.menu.menu
import eu.perfect.core.menu.slot
import eu.perfect.core.utils.chatInput
import eu.perfect.core.utils.extensions.displayName
import eu.perfect.core.utils.extensions.item
import eu.perfect.core.utils.extensions.msg
import org.bukkit.Material

val groupMenu = menu("§7Groups", 4, PerfectCore.instance, true) {
    var slots = 11
    for (group in PerfectCore.instance.groupManger.getAll()) {
        slot(slots, item(group.block_type).displayName(group.color.replace("&", "§") + group.name)) {
            slots++
            onClick {
                groupEditorMenu.openToPlayer(player)
            }
        }
    }

    slot(4, 4, item(Material.BOOK).displayName("§bCrie um grupo")) {
        onClick {
            player.msg("§bDigite §c'cancelar' §bpara cancelar a ação \n " +
                    "ou digite §fnome, tag, color, position, bloco")
            close()
            player.chatInput(PerfectCore.instance) {
                val data = it.split(" ")
                val name = data[0]
                val tag = data[1]
                val color = data[2]
                val position = data[3]
                val block = data[4]
                PerfectCore.instance.groupManger.newGroup(name, tag, color, position, block)
                player.msg("§bGrupo §f$name §bcriado com sucesso")
            }
        }
    }
}

val groupEditorMenu = menu("§7Group editor", 4, PerfectCore.instance) {

}