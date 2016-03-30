package io.github.otbproject.otb.plugin.content.data

import java.nio.file.Path

import io.github.otbproject.otb.core.Service
import io.github.otbproject.otb.core.fs.FileSystemObject
import io.github.otbproject.otb.plugin.content.ContentPlugin

final class ServiceData private[otb](plugins: Set[ContentPlugin], service: Service)
    extends Data[Service](_.serviceDF, plugins, service) with FileSystemObject {
    override def getPath: Path = ??? // TODO: impl
}
