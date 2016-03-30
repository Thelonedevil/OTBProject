package io.github.otbproject.otb.plugin.content.data

import java.nio.file.Path

import io.github.otbproject.otb.core.Service
import io.github.otbproject.otb.core.fs.FileSystemObject
import io.github.otbproject.otb.plugin.content.PluginDataFactory

final class ServiceData private[otb](plugins: PluginSet, service: Service)
    extends Data[Service]((factory: PluginDataFactory) => factory.getServiceDataFactory, plugins, service)
        with FileSystemObject {
    override def getPath: Path = ??? // TODO: impl
}
