package io.github.otbproject.otb.core

import java.nio.file.Path

import io.github.otbproject.otb.core.data.StaticChannelData
import io.github.otbproject.otb.core.fs.FileSystemObject

final class StaticChannel extends FileSystemObject {
    def getId: Int = ??? // TODO: impl

    def getData: StaticChannelData = ??? // TODO: impl

    override def getPath: Path = ??? // TODO: impl
}
