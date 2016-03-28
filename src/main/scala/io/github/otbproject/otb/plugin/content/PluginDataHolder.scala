package io.github.otbproject.otb.plugin.content

private[otb] trait PluginDataHolder[T] {
    def getPluginData: PluginDataMap
}
