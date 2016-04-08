package io.github.otbproject.otb.plugin.base

final case class PluginIdentifier[P <: Plugin](pluginClass: Class[P], pluginName: String)
