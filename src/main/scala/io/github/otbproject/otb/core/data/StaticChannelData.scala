package io.github.otbproject.otb.core.data

import io.github.otbproject.otb.core.StaticChannel

import scala.collection.immutable.ListSet

final class StaticChannelData private[otb](factoryProvider: FactoryProvider[StaticChannel], plugins: PluginSet,
                                           staticChannel: StaticChannel,
                                           readExecDataSupplier: () => ListSet[StaticChannelData])
    extends Data[StaticChannel](factoryProvider, plugins, staticChannel) {

}
