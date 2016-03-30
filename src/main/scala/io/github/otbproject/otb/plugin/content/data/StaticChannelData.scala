package io.github.otbproject.otb.plugin.content.data

import io.github.otbproject.otb.core.StaticChannel

import scala.collection.immutable.ListSet

final class StaticChannelData private[otb](plugins: PluginSet, staticChannel: StaticChannel,
                                           readExecDataSupplier: () => ListSet[StaticChannelData])
    extends Data[StaticChannel](_.getStaticChannelDataFactory, plugins, staticChannel) {
}
