package io.github.otbproject.otb.plugin.content.data

import java.nio.file.Path

import io.github.otbproject.otb.core.ServiceBot
import io.github.otbproject.otb.core.fs.FileSystemObject
import io.github.otbproject.otb.plugin.content.ContentPlugin

final class ServiceBotData private[otb](plugins: Set[ContentPlugin], bot: ServiceBot)
  extends Data[ServiceBot](_.serviceBotDF, plugins, bot) with FileSystemObject {

  override def getPath: Path = ??? // TODO: impl
}
