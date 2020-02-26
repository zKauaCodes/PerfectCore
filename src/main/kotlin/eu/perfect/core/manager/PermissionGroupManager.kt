package eu.perfect.core.manager

import com.okkero.skedule.SynchronizationContext
import com.okkero.skedule.schedule
import eu.perfect.core.PerfectCore
import eu.perfect.core.database.sql.SqlGlobal
import eu.perfect.core.database.table.PermissionGroupDAO
import eu.perfect.core.models.GroupModel
import eu.perfect.core.models.PermissionGroupModel
import eu.perfect.core.models.UserGroupModel
import org.bukkit.Bukkit
import org.jetbrains.exposed.sql.transactions.transaction

class PermissionGroupManager {

    private val cachedPermission = mutableMapOf<String, PermissionGroupModel>()

    fun findByPermission(perm: String): PermissionGroupModel? {
        if (cachedPermission.containsKey(perm)) {
            return cachedPermission[perm]
        }

        PerfectCore.instance.schedule(SynchronizationContext.ASYNC) {
            val permission = transaction(SqlGlobal.globalDatabase) {
                return@transaction PermissionGroupDAO.findByPermission(perm)
            }

            switchContext(SynchronizationContext.SYNC)
            cachedPermission[perm] = permission!!.asPermission()
        }
        return cachedPermission[perm]
    }

    fun newPermission(
        permission: String,
        group: GroupModel
     ) {
        PerfectCore.instance.schedule(SynchronizationContext.ASYNC) {
            val newPerm = transaction(SqlGlobal.globalDatabase) {
                return@transaction PermissionGroupDAO.newPermission(permission, group)
            }

            switchContext(SynchronizationContext.SYNC)
            cachedPermission[permission] = newPerm!!
        }
    }

    fun updatePermission(perm: PermissionGroupModel) {
        cachedPermission.remove(perm.permission)
        cachedPermission[perm.permission] = perm

        PerfectCore.instance.schedule(SynchronizationContext.ASYNC) {
            transaction(SqlGlobal.globalDatabase) {
                PermissionGroupDAO.findById(perm.id)?.apply {
                    permission = perm.permission
                    enabled = perm.enabled
                }
            }
        }
    }

    fun loadAllPermission() {
        val perms = transaction(SqlGlobal.globalDatabase) {
            return@transaction PermissionGroupDAO.all().toMutableList()
        }

        perms.forEach { perm ->
            cachedPermission[perm.permission] = perm.asPermission()
        }
    }

    fun verifyPermission(user: UserGroupModel, permission: String): Boolean {
        if (cachedPermission.containsKey(permission)) {
            return user.group.id == cachedPermission[permission]?.id
        }
        return false
    }

    fun deletePermission(permission: String) {
        cachedPermission.remove(permission)

        PerfectCore.instance.schedule(SynchronizationContext.ASYNC) {
            transaction(SqlGlobal.globalDatabase) {
                PermissionGroupDAO.findByPermission(permission)?.delete()
            }
        }
    }
}