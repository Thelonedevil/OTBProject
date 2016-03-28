package io.github.otbproject.otb.core.data

import io.github.otbproject.otb.plugin.content.{PluginDataHolder, PluginDataMap}

abstract class Data[T] private[data](pluginDataSupplier: T => PluginDataMap, t: T) extends PluginDataHolder[T] {
    private lazy val pluginData = pluginDataSupplier(t)

    final def getPluginData: PluginDataMap = pluginData
}
