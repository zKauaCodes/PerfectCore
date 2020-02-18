package eu.perfect.core.database.noSql

import com.mongodb.MongoClient
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import eu.perfect.core.PerfectCore
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

enum class ConnectionType {
    GLOBAL,
    SERVER;
}

class MongoDB(val connectionType: ConnectionType) {

    val configYml = PerfectCore.instance.config
    var client: MongoClient? = null

    fun init() {
        Logger.getLogger("org.mongodb.driver").level = Level.SEVERE

        if (connectionType == ConnectionType.GLOBAL) {
            val credential = MongoCredential.createCredential(
                configYml.getString("Mongo.user"),
                configYml.getString("Mongo.databaseGlobal"),
                configYml.getString("Mongo.password").toCharArray()
            )

            client = MongoClient(
                ServerAddress(
                    configYml.getString("Mongo.ip"),
                    configYml.getInt("Mongo.port")),
                listOf(credential)
            )
        } else if(connectionType == ConnectionType.SERVER) {
            val credential = MongoCredential.createCredential(
                configYml.getString("Mongo.user"),
                configYml.getString("Mongo.databaseServer"),
                configYml.getString("Mongo.password").toCharArray()
            )

            val client = MongoClient(
                ServerAddress(
                    configYml.getString("Mongo.ip"),
                    configYml.getInt("Mongo.port")),
                listOf(credential)
            )
        }
    }

    fun stopGlobal() {
        client?.close()
    }

    fun stop() {
        client?.close()
    }
}