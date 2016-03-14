package io.github.otbproject.otb.plugin.content

case class PluginDataTypeIdentifier[T](plugin: ContentPlugin, tClass: Class[T])
