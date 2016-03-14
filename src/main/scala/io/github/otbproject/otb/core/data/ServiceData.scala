package io.github.otbproject.otb.core.data

import java.nio.file.Path

import io.github.otbproject.otb.core.Service
import io.github.otbproject.otb.core.fs.FileSystemObject
import io.github.otbproject.otb.plugin.content.PluginDataMap

final class ServiceData private[otb](pluginDataSupplier: Service => PluginDataMap, service: Service)
    extends Data[Service](pluginDataSupplier, service) with FileSystemObject {
    override def getPath: Path = ??? // TODO: impl
}
