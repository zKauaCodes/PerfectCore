package eu.perfect.core

import eu.perfect.core.commandsDSL.compareTo
import eu.perfect.core.commandsDSL.settings
import eu.perfect.core.database.sql.SqlGlobal
import eu.perfect.core.database.table.*
import eu.perfect.core.models.PermissionGroupModel
import eu.perfect.core.models.UserGroupModel
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class PerfectCore : JavaPlugin() {

    companion object {
        lateinit var instance: PerfectCore
    }

    val cachedPerms = mutableMapOf<String, PermissionGroupModel>()

    override fun onEnable() {
        instance = this
        saveDefaultConfig()
        transaction(SqlGlobal.globalDatabase) {
            SchemaUtils.create(GroupTable, UserGroupTable, PermissionGroupTable)

            val ceo = GroupDAO.newGroup(
                "ceo",
                "&6[CEO] ",
                "&6",
                "a",
                "DIAMOND_BLOCK"
            )

            val perm = PermissionGroupDAO.newPermission(
                "perm.use",
                ceo?.asGroup()!!
            )

            val zKauaCodes = UserGroupDAO.newUserGroup(
                "zKauaCodes",
                ceo.asGroup()
            )

            Bukkit.getConsoleSender().sendMessage("§bGroup -> §f${ceo.asGroup().name} §bcriado")
            Bukkit.getConsoleSender().sendMessage("§bPermission -> §f${perm?.permission} §bcriado")
            Bukkit.getConsoleSender().sendMessage("§bUser -> §f${zKauaCodes?.name} §bcriado")

            loadAllPermission()

            verifyPermission(zKauaCodes!!, "perm.use")
        }
    }

    override fun onDisable() {

    }
    
    fun verifyPermission(user: UserGroupModel, permission: String) {
        if (cachedPerms.containsKey(permission)) {
            if (user.group.id == cachedPerms.get(permission)?.id) {
                Bukkit.getConsoleSender().sendMessage("§bUser -> §f${user.name} §btem a permissão §f${permission}")
            }else {
                Bukkit.getConsoleSender().sendMessage("§bUser -> §f${user.name} §bnão tem a permissão")
            }
        }
    }

    fun loadAllPermission() {
        val perms = transaction(SqlGlobal.globalDatabase) {
            val perm = PermissionGroupDAO.all().toMutableList()
            return@transaction perm
        }

        perms.forEach { perm ->
            cachedPerms[perm.asPermission().permission] = perm.asPermission()
        }
    }
}