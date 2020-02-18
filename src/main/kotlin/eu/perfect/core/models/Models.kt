package eu.perfect.core.models

import org.bukkit.Material
import java.util.*

class GroupModel(
    val id: Int,
    val name: String,
    val tag: String,
    val color: String,
    val position: String,
    val block_type: Material
)

class PermissionGroupModel(
    val id: Int,
    val permission: String,
    val enabled: Boolean,
    val group: GroupModel
)

class UserGroupModel(
    val id: Int,
    val name: String,
    val vip: Boolean = false,
    val permanent: Boolean = true,
    val time: Long,
    val group: GroupModel
)