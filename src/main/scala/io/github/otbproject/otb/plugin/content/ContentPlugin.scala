package io.github.otbproject.otb.plugin.content

import io.github.otbproject.otb.plugin.base.{Plugin, PluginInitializer}

abstract class ContentPlugin(initializer: PluginInitializer) extends Plugin(initializer) {
  type Factory <: PluginDataFactory

  val dataFactory: Factory
}
