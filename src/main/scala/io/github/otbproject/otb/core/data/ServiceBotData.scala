package io.github.otbproject.otb.core.data

import java.nio.file.Path

import io.github.otbproject.otb.core.ServiceBot
import io.github.otbproject.otb.core.fs.FileSystemObject
import io.github.otbproject.otb.plugin.content.PluginDataMap

final class ServiceBotData private[otb](pluginDataSupplier: ServiceBot => PluginDataMap, bot: ServiceBot)
    extends Data[ServiceBot](pluginDataSupplier, bot) with FileSystemObject {
    override def getPath: Path = ??? // TODO: impl
}
