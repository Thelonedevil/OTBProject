package io.github.otbproject.otb.core.data

import java.nio.file.Path

import io.github.otbproject.otb.core.Service
import io.github.otbproject.otb.core.fs.FileSystemObject

final class ServiceData private[otb](factoryProvider: FactoryProvider[Service], plugins: PluginSet, service: Service)
    extends Data[Service](factoryProvider, plugins, service) with FileSystemObject {
    override def getPath: Path = ??? // TODO: impl
}
