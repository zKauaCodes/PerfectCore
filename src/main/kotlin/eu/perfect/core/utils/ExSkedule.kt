package eu.perfect.core.utils

import com.okkero.skedule.BukkitDispatcher
import com.okkero.skedule.BukkitSchedulerController
import com.okkero.skedule.SynchronizationContext
import com.okkero.skedule.schedule
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext

fun WithPlugin<*>.schedule(
    initialContext: SynchronizationContext = SynchronizationContext.SYNC,
    co: suspend BukkitSchedulerController.() -> Unit
) = plugin.schedule(initialContext, co)

val BukkitSchedulerController.contextSync get() = SynchronizationContext.SYNC
val BukkitSchedulerController.contextAsync get() = SynchronizationContext.ASYNC

suspend fun BukkitSchedulerController.switchToSync() = switchContext(contextSync)
suspend fun BukkitSchedulerController.switchToAsync() = switchContext(contextAsync)

val WithPlugin<*>.BukkitDispatchers get() = JavaPlugin.getProvidingPlugin(plugin::class.java).BukkitDispatchers
val Plugin.BukkitDispatchers get() = PluginDispatcher(JavaPlugin.getProvidingPlugin(this::class.java))

inline class PluginDispatcher(val plugin: JavaPlugin) {
    val ASYNC get() = BukkitDispatcher(plugin, true)
    val SYNC get() = BukkitDispatcher(plugin, false)
}

// Take max millisecond in a tick

suspend fun BukkitSchedulerController.takeMaxPerTick(time: Millisecond) {
    val takeValues = getTakeValuesOrNull(context)

    if(takeValues == null) {
        // registering take max at current millisecond
        registerCoroutineContextTakes(context, time)
    } else {
        // checking if this exceeded the max time of execution
        if(takeValues.wasTimeExceeded()) {
            unregisterCoroutineContextTakes(context)
            waitFor(1)
        }
    }
}

internal val coroutineContextTakes = ConcurrentHashMap<CoroutineContext, TakeValues>()
internal data class TakeValues(val startTimeMilliseconds: Long, val takeTimeMillisecond: Long) {
    fun wasTimeExceeded() = System.currentTimeMillis() - startTimeMilliseconds - takeTimeMillisecond >= 0
}

internal fun getTakeValuesOrNull(
    coroutineContext: CoroutineContext
): TakeValues? = coroutineContextTakes[coroutineContext]

internal fun registerCoroutineContextTakes(
    coroutineContext: CoroutineContext,
    time: Millisecond
) {
    coroutineContextTakes.put(
        coroutineContext,
        TakeValues(System.currentTimeMillis(), time.toMillisecond())
    )
}

internal fun unregisterCoroutineContextTakes(
    coroutineContext: CoroutineContext
) {
    coroutineContextTakes.remove(coroutineContext)
}