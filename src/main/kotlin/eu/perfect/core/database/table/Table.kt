package eu.perfect.core.database.table

import org.jetbrains.exposed.dao.id.IntIdTable

object GroupTable : IntIdTable("group") {
    val name = varchar("name", 80).uniqueIndex()
    val tag = varchar("tag", 90)
    val color = varchar("color", 5)
    val position = varchar("position", 30).uniqueIndex()
    val block_type = varchar("block_type", 200)
}

object PermissionGroupTable: IntIdTable("permission_group") {
    val permission = varchar("permission", 255)
    val enable = bool("enable").default(true)
    val group_id = reference("group_id", GroupTable)
}

object UserGroupTable : IntIdTable("users_group") {
    val name = varchar("name", 225).uniqueIndex()
    val vip = bool("vip").default(false)
    val permanent = bool("permanent").default(true)
    val time = long("time").default(0)
    val group_id = reference("group_id", GroupTable)
}


