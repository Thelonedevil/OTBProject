package io.github.otbproject.otb.plugin.content.data

import io.github.otbproject.otb.plugin.content._

/**
  *
  * @param factoryProvider
  * @param plugins see [[PluginSet]] for recommended Set type
  * @param t
  * @tparam T
  */
abstract class Data[T] private[data](factoryProvider: FactoryProvider[T], plugins: PluginSet,
                                     t: T) extends PluginDataHolder[T] {
    private lazy val pluginData = supplyData(t, plugins)

    final def getPluginData: PluginDataMap = pluginData

    private def supplyData(t: T, plugins: PluginSet): PluginDataMap = {
        val builder = PluginDataMap.newBuilder
        val m = plugins.map((p: ContentPlugin[PluginDataFactory[_, _, _, _, _]]) => factoryProvider(p.getDataFactory))

        // I think this prevents repeated initializations
        m.takeWhile((factory: DataFactory[T, _ <: PluginData]) => {
            try {
                factory.provideData(t, builder)
                true
            } catch {
                case e: InitializationLoopException =>
                    ??? // TODO: Log/warn
                    false
                case e: Exception => ??? // TODO: Log/warn
                    true
            }
        })

        m.foreach((factory: DataFactory[T, _ <: PluginData]) => factory.resetInitializer())

        builder.build
    }
}

// For parameter and import convenience
private[data] trait FactoryProvider[T] extends (PluginDataFactory[_, _, _, _, _] => DataFactory[T, _ <: PluginData])

/**
  * SHOULD BE A [[scala.collection.immutable.ListSet]]
  */
private[data] sealed trait PluginSet extends Set[AnyPlugin]
