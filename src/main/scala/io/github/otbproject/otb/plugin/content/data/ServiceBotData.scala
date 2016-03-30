package io.github.otbproject.otb.plugin.content.data

import java.nio.file.Path

import io.github.otbproject.otb.core.ServiceBot
import io.github.otbproject.otb.core.fs.FileSystemObject
import io.github.otbproject.otb.plugin.content.PluginDataFactory

final class ServiceBotData private[otb](plugins: PluginSet, bot: ServiceBot)
    extends Data[ServiceBot]((factory: PluginDataFactory) => factory.getServiceBotDataFactory, plugins, bot)
        with FileSystemObject {
    override def getPath: Path = ??? // TODO: impl
}
