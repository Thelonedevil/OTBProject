package io.github.otbproject.otb.core

import java.nio.file.Path

import io.github.otbproject.otb.core.fs.FileSystemObject
import io.github.otbproject.otb.plugin.content.data.StaticChannelData

final class StaticChannel extends FileSystemObject {
  def getId: Int = ??? // TODO: impl

  def getData: StaticChannelData = ??? // TODO: impl

  override def getPath: Path = ??? // TODO: impl
}
