package io.github.otbproject.otb.plugin.content.data

import io.github.otbproject.otb.core.StaticChannel
import io.github.otbproject.otb.plugin.content.{PluginData, PluginDataFactory}

import scala.collection.immutable.ListSet

final class StaticChannelData private[otb](plugins: PluginSet, staticChannel: StaticChannel,
                                           readExecDataSupplier: () => ListSet[StaticChannelData])
    extends Data[StaticChannel]((factory: PluginDataFactory) => factory.getStaticChannelDataFactory,
        plugins, staticChannel) {

}
