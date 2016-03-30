package io.github.otbproject.otb.plugin.content.data

import io.github.otbproject.otb.plugin.content._

private[data] abstract class Data[T](provider: PluginDataFactory => DataFactory[T, _ <: PluginData],
                                     plugins: Set[ContentPlugin], t: T) extends PluginDataHolder[T] {
    private lazy val pluginData: PluginDataMap = supplyData(t, plugins)

    final def getPluginData: PluginDataMap = pluginData

    private def supplyData(t: T, plugins: Set[ContentPlugin]): PluginDataMap = {
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
