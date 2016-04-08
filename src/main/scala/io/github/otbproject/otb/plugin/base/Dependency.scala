package io.github.otbproject.otb.plugin.base

import io.github.otbproject.otb.misc.CoreVersion

@throws[IllegalArgumentException]
final case class Dependency[P <: Plugin](identifier: PluginIdentifier[P],
                                         minVersion: CoreVersion,
                                         maxMajorVersion: Int) {
  // Make sure maxMajorVersion is valid
  if (maxMajorVersion < 0) {
    throw new IllegalArgumentException("Maximum major version must be non-negative.")
  }

  def this(identifier: PluginIdentifier[P], minVersion: CoreVersion) =
    this(identifier, minVersion, minVersion.major + 1)
}
