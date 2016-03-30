package io.github.otbproject.otb.plugin.content.data

import io.github.otbproject.otb.core.StaticBot

final class StaticBotData private[otb](plugins: PluginSet, staticBot: StaticBot)
    extends Data[StaticBot](_.getStaticBotDataFactory, plugins, staticBot) {
}
