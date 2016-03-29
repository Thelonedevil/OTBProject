package io.github.otbproject.otb.plugin.content

private[content] trait PluginDataHolder[T] {
    def getPluginData: PluginDataMap
}
