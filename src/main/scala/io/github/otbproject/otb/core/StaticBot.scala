package io.github.otbproject.otb.core

import java.nio.file.Path

import io.github.otbproject.otb.core.data.StaticBotData
import io.github.otbproject.otb.core.fs.FileSystemObject

final class StaticBot extends FileSystemObject {
    def getId: Int = ??? // TODO: impl

    def getData: StaticBotData = ??? // TODO: impl

    override def getPath: Path = ??? // TODO: impl
}
