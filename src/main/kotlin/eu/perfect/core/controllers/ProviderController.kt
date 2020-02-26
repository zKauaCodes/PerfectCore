package eu.perfect.core.controllers

import eu.perfect.core.PerfectCore
import eu.perfect.core.providePerfectCore
import eu.perfect.core.utils.KClassComparator
import eu.perfect.core.utils.extensions.KListener
import eu.perfect.core.utils.extensions.event
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.plugin.Plugin
import java.util.*
import kotlin.reflect.KClass

internal fun provideProviderController() = providePerfectCore().providerController

internal class ProviderController(
        override val plugin: PerfectCore
) : KListener<PerfectCore>, PerfectController {

    private val providerTree = TreeMap<String, TreeMap<KClass<*>, Any>>()

    fun register(plugin: Plugin, any: Any): Boolean {
        return providerTree.getOrPut(plugin.name, { TreeMap(KClassComparator) })
                .putIfAbsent(any::class, any) == null
    }

    fun unregister(plugin: Plugin, any: Any): Boolean {
        return providerTree.get(plugin.name)?.remove(any::class) == true
    }

    fun <T : Any> find(plugin: Plugin, kclass: KClass<T>): T {
        return providerTree.get(plugin.name)?.get(kclass) as T
    }

    override fun onEnable() {
        event<PluginDisableEvent> {
            providerTree.remove(plugin.name)
        }
    }
}