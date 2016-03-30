package io.github.otbproject.otb.plugin.content.data

import io.github.otbproject.otb.core.StaticBot
import io.github.otbproject.otb.plugin.content.ContentPlugin

final class StaticBotData private[otb](plugins: Set[ContentPlugin], staticBot: StaticBot)
    extends Data[StaticBot](_.staticBotDF, plugins, staticBot) {
}
