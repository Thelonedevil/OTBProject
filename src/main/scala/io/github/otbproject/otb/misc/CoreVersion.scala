package io.github.otbproject.otb.misc

@throws[IllegalArgumentException]
final case class CoreVersion(major: Int, minor: Int, patch: Int) extends Ordered[CoreVersion] {
  // Ensure major, minor, and patch are non-negative
  if (major < 0 || minor < 0 || patch < 0) {
    throw new IllegalArgumentException("Major, minor, and patch values for a version must be non-negative.")
  }

  private lazy val ordering = Ordering.Tuple3(Ordering.Int, Ordering.Int, Ordering.Int)

  override def compare(that: CoreVersion): Int = {
    ordering.compare((major, minor, patch), (that.major, that.minor, that.patch))
  }
}
