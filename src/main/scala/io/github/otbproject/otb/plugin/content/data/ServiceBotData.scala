package io.github.otbproject.otb.plugin.content.data

import java.nio.file.Path

import io.github.otbproject.otb.core.ServiceBot
import io.github.otbproject.otb.core.fs.FileSystemObject

final class ServiceBotData private[otb](factoryProvider: FactoryProvider[ServiceBot], plugins: PluginSet,
                                        bot: ServiceBot)
    extends Data[ServiceBot](factoryProvider, plugins, bot) with FileSystemObject {
    override def getPath: Path = ??? // TODO: impl
}
