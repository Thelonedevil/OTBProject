package io.github.otbproject.otb.core.data

import io.github.otbproject.otb.core.StaticChannel
import io.github.otbproject.otb.plugin.content.PluginDataMap

import scala.collection.immutable.ListSet

final class StaticChannelData private[otb](pluginDataSupplier: StaticChannel => PluginDataMap, staticChannel: StaticChannel,
                                           readExecDataSupplier: () => ListSet[StaticChannelData])
    extends Data[StaticChannel](pluginDataSupplier, staticChannel) {

}
