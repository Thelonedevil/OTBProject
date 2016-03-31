package io.github.otbproject.otb.misc

import java.util.function.Function

object JLambda {
    def toFunction[T, R](func: Function[T, R]): T => R = (t: T) => func(t)
}
