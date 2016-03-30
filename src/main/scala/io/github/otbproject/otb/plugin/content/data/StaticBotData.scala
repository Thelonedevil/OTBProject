package io.github.otbproject.otb.plugin.content.data

import io.github.otbproject.otb.core.StaticBot
import io.github.otbproject.otb.plugin.content.{PluginData, PluginDataFactory}

final class StaticBotData private[otb](plugins: PluginSet, staticBot: StaticBot)
    extends Data[StaticBot]((factory: PluginDataFactory) => factory.getStaticBotDataFactory, plugins, staticBot) {

}
