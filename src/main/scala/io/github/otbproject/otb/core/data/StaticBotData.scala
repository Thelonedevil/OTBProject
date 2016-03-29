package io.github.otbproject.otb.core.data

import io.github.otbproject.otb.core.StaticBot

final class StaticBotData private[otb](factoryProvider: FactoryProvider[StaticBot], plugins: PluginSet,
                                       staticBot: StaticBot)
    extends Data[StaticBot](factoryProvider, plugins, staticBot) {

}
