package eu.perfect.core.manager

import com.okkero.skedule.SynchronizationContext
import com.okkero.skedule.schedule
import eu.perfect.core.PerfectCore
import eu.perfect.core.database.sql.SqlGlobal
import eu.perfect.core.database.table.GroupDAO
import eu.perfect.core.database.table.PermissionGroupDAO
import eu.perfect.core.models.GroupModel
import org.bukkit.Bukkit
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.stream.Collectors

class GroupManager {

    private val cachedGroups = mutableMapOf<String, GroupModel>()

    fun findGroupByName(name: String): GroupModel? {
        if (cachedGroups.containsKey(name)) {
            return cachedGroups[name]
        }
        PerfectCore.instance.schedule(SynchronizationContext.ASYNC) {
            val group = transaction(SqlGlobal.globalDatabase) {
                return@transaction GroupDAO.findByName(name)
            }
            switchContext(SynchronizationContext.SYNC)
            cachedGroups[name] = group?.asGroup()!!
        }
        return cachedGroups[name]
    }

    fun newGroup(
        name: String,
        tag: String,
        color: String,
        position: String,
        block_type: String
    ) {
        PerfectCore.instance.schedule(SynchronizationContext.ASYNC) {
            val newGroup = transaction(SqlGlobal.globalDatabase) {
                return@transaction GroupDAO.newGroup(name, tag, color, position, block_type)
            }
            switchContext(SynchronizationContext.SYNC)
            Bukkit.getConsoleSender().sendMessage("§bDEBUG ANTES DE SETA NO CACHE")
            cachedGroups[name] = newGroup?.asGroup()!!
        }
        Bukkit.getConsoleSender().sendMessage("§bGrupo §f-> ${cachedGroups[name]?.name}")
    }

    fun updateGroupDatabase(groupModel: GroupModel) {
        cachedGroups.remove(groupModel.name)
        cachedGroups[groupModel.name] = groupModel

        PerfectCore.instance.schedule(SynchronizationContext.ASYNC) {
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
    }

    fun loadAllGroup() {
        val groups = transaction(SqlGlobal.globalDatabase) {
            return@transaction GroupDAO.all().toMutableList()
        }

        groups.forEach { group ->
            cachedGroups[group.name] = group.asGroup()
        }
    }

    fun getAll(): List<GroupModel> {
        return cachedGroups.values.stream().collect(Collectors.toList())
    }


    fun deleteGroup(groupModel: GroupModel) {
        cachedGroups.remove(groupModel.name)

        PerfectCore.instance.schedule(SynchronizationContext.ASYNC) {
            transaction(SqlGlobal.globalDatabase) {
                GroupDAO.findById(groupModel.id)?.delete()
            }
        }
    }
}