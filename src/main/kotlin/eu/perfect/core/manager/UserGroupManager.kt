package eu.perfect.core.manager

import com.okkero.skedule.SynchronizationContext
import com.okkero.skedule.schedule
import eu.perfect.core.PerfectCore
import eu.perfect.core.database.sql.SqlGlobal
import eu.perfect.core.database.table.UserGroupDAO
import eu.perfect.core.database.table.UserGroupTable
import eu.perfect.core.models.GroupModel
import eu.perfect.core.models.UserGroupModel
import org.jetbrains.exposed.sql.transactions.transaction

class UserGroupManager {

    private val cachedUser = mutableMapOf<String, UserGroupModel>()

    fun findByName(name: String): UserGroupModel {
        if (cachedUser.containsKey(name)) {
            return cachedUser[name]!!
        }

        PerfectCore.instance.schedule(SynchronizationContext.ASYNC) {
            val user = transaction(SqlGlobal.globalDatabase) {
                return@transaction UserGroupDAO.findByName(name)
            }

            switchContext(SynchronizationContext.SYNC)
            cachedUser[name] = user!!
        }
        return cachedUser[name]!!
    }

    fun newUserGroup(
        name: String,
        group: GroupModel
    ) {
        PerfectCore.instance.schedule(SynchronizationContext.ASYNC) {
            val user = transaction(SqlGlobal.globalDatabase) {
                return@transaction UserGroupDAO.newUserGroup(name, group)
            }

            switchContext(SynchronizationContext.SYNC)
            cachedUser[name] = user!!
        }
    }

    fun updateUserGroup(user: UserGroupModel) {
        cachedUser.remove(user.name)
        cachedUser[user.name] = user

        PerfectCore.instance.schedule(SynchronizationContext.ASYNC) {
            val userUpdated = transaction(SqlGlobal.globalDatabase) {
                return@transaction UserGroupDAO.updateUserGroup(user, user.group)
            }
        }
    }

    fun deleteUserGroup(name: String) {
        cachedUser.remove(name)

        PerfectCore.instance.schedule(SynchronizationContext.ASYNC) {
            transaction(SqlGlobal.globalDatabase) {
                UserGroupDAO.find { UserGroupTable.name eq name }.firstOrNull()?.delete()
            }
        }
    }
}