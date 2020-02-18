package eu.perfect.core.database.sql

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import eu.perfect.core.PerfectCore
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.vendors.MysqlDialect

object SqlGlobal {

    private val configYml = PerfectCore.instance.config

    val hikariConfig by lazy {
        val config = HikariConfig()

        config.jdbcUrl = "jdbc:mysql://${configYml.getString("Mysql.ip", "localhost")}:" +
                "${configYml.getString("Mysql.port", "3306")}/" +
                configYml.getString("Mysql.databaseGlobal", "perfect")

        config.username = configYml.getString("Mysql.user", "root")
        config.password = configYml.getString("Mysql.password", "perfectnetwork")

        config.driverClassName = "com.mysql.jdbc.Driver"

        config.maximumPoolSize = 10
        config.addDataSourceProperty("cachePrepStmts", "true")
        config.addDataSourceProperty("prepStmtCacheSize", "250")
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")

        return@lazy config
    }

    private val datasource by lazy { HikariDataSource(hikariConfig) }

    val globalDatabase by lazy { Database.connect(datasource) }
}