package eu.perfect.core.database.noSql

import eu.perfect.core.PerfectCore
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig

class Redis {

    private val configYml = PerfectCore.instance.config

    //TODO terminar a parte de redis mais tarde

    fun init() {
        val jedisPool = JedisPool(
            JedisPoolConfig(),
            configYml.getString("Redis.ip"),
            configYml.getInt("Redis.port")
            )

        val jedis = jedisPool.resource
        jedis.auth(configYml.getString("Redis.password"))
    }
}