package eu.perfect.core.database.table

import eu.perfect.core.models.GroupModel
import eu.perfect.core.models.PermissionGroupModel
import eu.perfect.core.models.UserGroupModel
import org.bukkit.Material
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class GroupDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<GroupDAO>(GroupTable) {

        fun findByName(name: String): GroupDAO? {
            return find { GroupTable.name eq name }
                .firstOrNull()
        }

        fun newGroup(
            name: String,
            tag: String,
            color: String,
            position: String,
            block_type: String
        ): GroupDAO? {
            return if (findByName(name) == null) {
                new {
                    this.name = name
                    this.tag = tag
                    this.color = color
                    this.position = position
                    this.block_type = block_type
                }
            }else null
        }
    }

     var name by GroupTable.name
     var tag by GroupTable.tag
     var color by GroupTable.color
     var position by GroupTable.position
     var block_type by GroupTable.block_type

    fun asGroup() = GroupModel(
        id.value, name, tag, color, position, Material.valueOf(block_type)
    )
}

class PermissionGroupDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PermissionGroupDAO>(PermissionGroupTable) {
        fun newPermission(permission: String, groupModel: GroupModel): PermissionGroupModel? {
            return GroupDAO.findById(groupModel.id)?.let {
                newPermissionGroup(permission, it)
            }
        }

         fun newPermissionGroup(permission: String, group: GroupDAO): PermissionGroupModel {
            return new {
                this.permission = permission
                this.group_id = group
            }.asPermission()
        }
    }

     var permission by PermissionGroupTable.permission
     var enabled by PermissionGroupTable.enable
     var group_id by GroupDAO referencedOn PermissionGroupTable.group_id

    fun asPermission() = PermissionGroupModel(
        id.value,
        permission,
        enabled,
        group_id.asGroup()
    )
}


class UserGroupDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserGroupDAO>(UserGroupTable) {

         fun findGroupById(id: Int): UserGroupModel? {
            return find { UserGroupTable.group_id eq id }
                .firstOrNull()
                ?.asUserGroup()
        }

        fun findByName(name: String): UserGroupModel? {
            return find { UserGroupTable.name eq name }
                .firstOrNull()
                ?.asUserGroup()
        }

        fun findByGroup(groupModel: GroupModel): UserGroupModel? {
            return findGroupById(groupModel.id)
        }

        fun  newUserGroup(name: String, groupModel: GroupModel): UserGroupModel? {
            return GroupDAO.findById(groupModel.id)?.let {
                newUserGroup(name, it)
            }
        }

         fun newUserGroup(
            name: String,
            group: GroupDAO
        ): UserGroupModel {
            return new {
                this.name = name
                this.group_id = group
            }.asUserGroup()
        }
    }

     var name by UserGroupTable.name
     var vip by UserGroupTable.vip
     var permanent by UserGroupTable.permanent
     var time by UserGroupTable.time
     var group_id by GroupDAO referencedOn UserGroupTable.group_id

    fun asUserGroup() = UserGroupModel(
        id.value,
        name,
        vip,
        permanent,
        time,
        group_id.asGroup()
    )
}