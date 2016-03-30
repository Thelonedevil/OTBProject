package io.github.otbproject.otb.plugin.content

import io.github.otbproject.otb.plugin.Plugin

trait ContentPlugin extends Plugin {
    type F <: PluginDataFactory

    protected val pluginDataFactory: F

    final def getDataFactory: F = pluginDataFactory
}
