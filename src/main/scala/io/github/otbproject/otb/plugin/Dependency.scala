package io.github.otbproject.otb.plugin

import io.github.otbproject.otb.misc.CoreVersion

final case class Dependency(name: String, minVersion: CoreVersion, maxMajorVersion: Int) {
  def this(name: String, minVersion: CoreVersion) = this(name, minVersion, minVersion.major + 1)
}
