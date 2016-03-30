package io.github.otbproject.otb.plugin.content.data

import java.nio.file.Path

import io.github.otbproject.otb.core.ServiceBot
import io.github.otbproject.otb.core.fs.FileSystemObject

final class ServiceBotData private[otb](plugins: PluginSet, bot: ServiceBot)
    extends Data[ServiceBot](_.getServiceBotDataFactory, plugins, bot) with FileSystemObject {

    override def getPath: Path = ??? // TODO: impl
}
