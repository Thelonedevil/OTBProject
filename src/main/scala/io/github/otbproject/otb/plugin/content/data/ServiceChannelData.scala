package io.github.otbproject.otb.plugin.content.data

import io.github.otbproject.otb.core.ServiceChannel

final class ServiceChannelData private[otb](factoryProvider: FactoryProvider[ServiceChannel], plugins: PluginSet,
                                            channel: ServiceChannel)
    extends Data[ServiceChannel](factoryProvider, plugins, channel) {

}
