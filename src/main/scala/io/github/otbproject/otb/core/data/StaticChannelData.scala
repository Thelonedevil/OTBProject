package io.github.otbproject.otb.core.data

import io.github.otbproject.otb.core.StaticChannel
import io.github.otbproject.otb.plugin.content.PluginDataMap

import scala.collection.immutable.ListSet

final class StaticChannelData private[otb](primaryData: PluginDataMap, readExecData: ListSet[Data])
    extends Data[StaticChannel](primaryData) {

}
