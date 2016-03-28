package io.github.otbproject.otb.plugin.content

import io.github.otbproject.otb.plugin.Plugin

trait ContentPlugin[F <: PluginDataFactory[_, _, _, _, _]] extends Plugin {
    protected val pluginDataFactory: F

    final def getDataFactory: F = pluginDataFactory
}
