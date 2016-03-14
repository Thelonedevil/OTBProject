package io.github.otbproject.otb.core.data

import io.github.otbproject.otb.core.ServiceChannel
import io.github.otbproject.otb.plugin.content.PluginDataMap

final class ServiceChannelData private[otb](pluginDataSupplier: ServiceChannel => PluginDataMap, channel: ServiceChannel)
    extends Data[ServiceChannel](pluginDataSupplier, channel) {

}
