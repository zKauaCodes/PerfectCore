package eu.perfect.core

import eu.perfect.core.commandsDSL.compareTo
import eu.perfect.core.commandsDSL.settings
import eu.perfect.core.controllers.MenuController
import eu.perfect.core.controllers.PerfectController
import eu.perfect.core.controllers.PlayerController
import eu.perfect.core.controllers.ProviderController
import eu.perfect.core.database.sql.SqlGlobal
import eu.perfect.core.database.table.*
import eu.perfect.core.inventorys.groupMenu
import eu.perfect.core.manager.GroupManager
import eu.perfect.core.manager.PermissionGroupManager
import eu.perfect.core.manager.UserGroupManager
import eu.perfect.core.utils.extensions.registerEvents
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

internal fun providePerfectCore(): PerfectCore {
    return Bukkit.getServer().pluginManager.getPlugin("PerfectCore") as PerfectCore?
        ?: throw IllegalAccessException("The plugin KotlinBukkitAPI is not loaded yet")
}


class PerfectCore : JavaPlugin() {

    companion object {
        lateinit var instance: PerfectCore
            private set
    }

    internal val playerController = PlayerController(this)
    internal val providerController = ProviderController(this)
    internal val menuController = MenuController(this)

    private val controllers = listOf<PerfectController>(
        playerController, menuController, providerController
    )

    lateinit var groupManger: GroupManager
    lateinit var permissionManager: PermissionGroupManager
    lateinit var userManager: UserGroupManager

    override fun onEnable() {
        instance = this
        saveDefaultConfig()
        registers()
        transaction(SqlGlobal.globalDatabase) {
            SchemaUtils.create(GroupTable, UserGroupTable, PermissionGroupTable)

            groupManger.loadAllGroup()
            permissionManager.loadAllPermission()
        }

        commands()
    }

    override fun onDisable() {

    }

    fun registers() {
        for (controller in controllers) {
            controller.onEnable()

            if(controller is Listener)
                registerEvents(controller)
        }

        groupManger = GroupManager()
        permissionManager = PermissionGroupManager()
        userManager = UserGroupManager()
    }

    val group = settings("group")

    fun commands() {
        group > {
            groupMenu.openToPlayer(sender)
        }
    }
}