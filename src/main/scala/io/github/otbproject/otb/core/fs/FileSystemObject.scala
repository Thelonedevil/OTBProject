package io.github.otbproject.otb.core.fs

import java.nio.file.Path

trait FileSystemObject {
    def getPath: Path
}
