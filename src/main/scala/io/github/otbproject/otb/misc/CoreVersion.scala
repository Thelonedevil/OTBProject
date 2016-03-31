package io.github.otbproject.otb.misc

final case class CoreVersion(major: Int, minor: Int, patch: Int) extends Ordered[CoreVersion] {
    private lazy val ordering = Ordering.Tuple3(Ordering.Int, Ordering.Int, Ordering.Int)

    override def compare(that: CoreVersion): Int = {
        ordering.compare((major, minor, patch), (that.major, that.minor, that.patch))
    }
}
