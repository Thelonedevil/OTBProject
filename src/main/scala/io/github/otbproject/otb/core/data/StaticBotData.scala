package io.github.otbproject.otb.core.data

import io.github.otbproject.otb.core.StaticBot
import io.github.otbproject.otb.plugin.content.PluginDataMap

final class StaticBotData private[otb](pluginDataSupplier: StaticBot => PluginDataMap, staticBot: StaticBot)
    extends Data[StaticBot](pluginDataSupplier, staticBot) {

}
