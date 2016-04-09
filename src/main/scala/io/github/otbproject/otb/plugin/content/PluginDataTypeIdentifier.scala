package io.github.otbproject.otb.plugin.content

private[content] final case class PluginDataTypeIdentifier[T <: PluginData](plugin: ContentPlugin, tClass: Class[T])
