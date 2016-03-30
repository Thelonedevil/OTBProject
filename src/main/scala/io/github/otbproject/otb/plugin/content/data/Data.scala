package io.github.otbproject.otb.plugin.content.data

import io.github.otbproject.otb.plugin.content._

/**
  *
  * @param plugins see [[PluginSet]] for recommended Set type
  * @param t
  * @tparam T
  */
private[data] abstract class Data[T](provider: PluginDataFactory => DataFactory[T, _ <: PluginData],
                                     plugins: PluginSet, t: T) extends PluginDataHolder[T] {
    private lazy val pluginData: PluginDataMap = supplyData(t, plugins)

    final def getPluginData: PluginDataMap = pluginData

    private def supplyData(t: T, plugins: PluginSet): PluginDataMap = {
        val builder = PluginDataMap.newBuilder
        val m = plugins.map((plugin: ContentPlugin) => provider(plugin.getDataFactory))

        // I think this prevents repeated initializations, assuming a
        // consistent iteration order
        m.takeWhile((factory: DataFactory[T, _ <: PluginData]) => {
            try {
                factory.provideData(t, builder)
                true
            } catch {
                case e: InitializationLoopException =>
                    ??? // TODO: Log/warn
                    false
                case e: Exception =>
                    ??? // TODO: Log/warn
                    true
            }
        })

        m.foreach(_.resetInitializer())

        builder.build
    }
}

// For parameter and import convenience
/**
  * SHOULD BE A [[scala.collection.immutable.ListSet]]
  */
private[data] sealed trait PluginSet extends Set[ContentPlugin]
