package io.github.otbproject.otb.plugin.content

import io.github.otbproject.otb.plugin.Plugin

abstract class ContentPlugin extends Plugin {
    final def getDataFrom[T <: PluginData](dataHolder: PluginDataHolder, tClass: Class[T]): Option[T] = {
        dataHolder.getPluginData.get(this, tClass)
    }
}
