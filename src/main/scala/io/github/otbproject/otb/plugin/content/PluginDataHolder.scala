package io.github.otbproject.otb.plugin.content

private[content] trait PluginDataHolder[T] {
  private[content] def getPluginData: PluginDataMap
}
