package io.github.otbproject.otb.plugin.content.data

import java.util.concurrent.atomic.AtomicBoolean

import io.github.otbproject.otb.plugin.content._

private[data] abstract class Data[T](provider: PluginDataFactory => DataFactory[T, _ <: PluginData],
                                     plugins: Set[ContentPlugin], t: T) extends PluginDataHolder[T] {
  private lazy val pluginData: PluginDataMap = supplyData(t, plugins)
  private val startedInitializing = new AtomicBoolean(false)

  private[content] final def getPluginData: PluginDataMap = pluginData

  @throws[RepeatedInitializationException]
  private def supplyData(t: T, plugins: Set[ContentPlugin]): PluginDataMap = {
    // Prevent repeated initialization. Thread-safe in case some plugin
    // spawns a thread and tries to access the data during initialization.
    if (startedInitializing.getAndSet(true)) {
      throw new RepeatedInitializationException(this)
    }

    val builder = PluginDataMap.newBuilder

    plugins.toStream
      .map((plugin: ContentPlugin) => provider(plugin.getDataFactory))
      .foreach((factory: DataFactory[T, _ <: PluginData]) => {
        try {
          factory.provideData(t, builder)
        } catch {
          case e: Exception => ??? // TODO: Log
        }
      })

    builder.build
  }
}

final class RepeatedInitializationException private[content](data: Data[_])
  extends IllegalStateException("Attempted to generate PluginData for [" + data + "] more than once. " +
                                  "This was probably caused by some plugin attempting to access data from the object " +
                                  "during its initialization.")
