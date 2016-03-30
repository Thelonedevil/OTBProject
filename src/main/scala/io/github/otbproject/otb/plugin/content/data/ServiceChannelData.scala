package io.github.otbproject.otb.plugin.content.data

import io.github.otbproject.otb.core.ServiceChannel
import io.github.otbproject.otb.plugin.content.ContentPlugin

final class ServiceChannelData private[otb](plugins: Set[ContentPlugin], channel: ServiceChannel)
    extends Data[ServiceChannel](_.serviceChannelDF, plugins, channel) {
}
