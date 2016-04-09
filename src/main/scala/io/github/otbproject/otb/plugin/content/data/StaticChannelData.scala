package io.github.otbproject.otb.plugin.content.data

import io.github.otbproject.otb.core.StaticChannel
import io.github.otbproject.otb.plugin.content.ContentPlugin

import scala.collection.immutable.ListSet

final class StaticChannelData private[otb](plugins: Set[ContentPlugin], staticChannel: StaticChannel,
                                           readExecDataSupplier: () => ListSet[StaticChannelData])
  extends Data[StaticChannel](_.staticChannelDF, plugins, staticChannel) {
}
