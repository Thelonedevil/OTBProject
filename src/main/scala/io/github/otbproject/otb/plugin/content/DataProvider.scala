package io.github.otbproject.otb.plugin.content

abstract class DataProvider[T, H <: PluginDataHolder, P <: PluginData](plugin: ContentPlugin, initializer: T => P,
                                                                       hClass: Class[H], pClass: Class[P]) {
    final def provideData(initData: T): P = initializer.apply(initData)

    final def getData(h: H): P = h.getPluginData.get(plugin, pClass).get
}
