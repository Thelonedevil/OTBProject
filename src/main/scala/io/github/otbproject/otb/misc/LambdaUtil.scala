package io.github.otbproject.otb.misc

import java.util.function.{Function, Supplier, UnaryOperator}

object LambdaUtil {
    def s2jUnaryOperator[T](func: T => T): UnaryOperator[T] = {
        new UnaryOperator[T] {
            override def apply(t: T): T = func.apply(t)
        }
    }

    def s2jFunction[T, R](func: T => R): Function[T, R] = {
        new Function[T, R] {
            override def apply(t: T): R = func(t)
        }
    }

    def s2jSupplier[T](func: () => T): Supplier[T] = {
        new Supplier[T] {
            override def get(): T = func()
        }
    }

    def j2sFunction[T, R](func: Function[T, R]): T => R = (t: T) => func.apply(t)
}
