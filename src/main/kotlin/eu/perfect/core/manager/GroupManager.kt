package eu.perfect.core.manager

import eu.perfect.core.database.sql.SqlGlobal
import eu.perfect.core.database.table.GroupDAO
import eu.perfect.core.models.GroupModel
import org.jetbrains.exposed.sql.transactions.transaction

class GroupManager {

    private val cachedGroups = mutableMapOf<String, GroupModel>()

    //TODO adicionar o metodo tbm find

    fun newGroup(
        name: String,
        tag: String,
        color: String,
        position: String,
        block_type: String
    ) {
        val newGroup = transaction(SqlGlobal.globalDatabase) {
            return@transaction GroupDAO.newGroup(name, tag, color, position, block_type)
            //TODO adicionar async
        }
        cachedGroups[name] = newGroup?.asGroup()!!
    }

    fun updateGroup(groupModel: GroupModel) {
        cachedGroups[groupModel.name] = groupModel
    }

    fun updateGroupDatabase(groupModel: GroupModel) {
        transaction(SqlGlobal.globalDatabase) {
            GroupDAO.findById(groupModel.id)?.apply {
                name = groupModel.name
                tag = groupModel.tag
                color = groupModel.color
                position = groupModel.position
                block_type = groupModel.block_type.name
            }
        }
    }

    fun deleteGroup(groupModel: GroupModel) {
        cachedGroups.remove(groupModel.name)

        transaction {
            GroupDAO.findById(groupModel.id)?.delete()
            // TODO async dps
        }
    }
}