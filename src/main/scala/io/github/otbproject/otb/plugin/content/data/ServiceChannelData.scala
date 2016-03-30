package io.github.otbproject.otb.plugin.content.data

import io.github.otbproject.otb.core.ServiceChannel
import io.github.otbproject.otb.plugin.content.PluginDataFactory

final class ServiceChannelData private[otb](plugins: PluginSet, channel: ServiceChannel)
    extends Data[ServiceChannel]((factory: PluginDataFactory) => factory.getServiceChannelDataFactory,
        plugins, channel) {

}
