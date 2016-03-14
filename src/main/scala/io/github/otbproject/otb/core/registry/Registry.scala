package io.github.otbproject.otb.core.registry

import java.util.concurrent.atomic.AtomicReference

import io.github.otbproject.otb.misc.LambdaUtil

final class Registry[T] {
    private val services: AtomicReference[Set[T]] = new AtomicReference[Set[T]](Set())

    @throws[AlreadyRegisteredException]
    def register(t: T): Unit = {
        services.getAndUpdate(LambdaUtil.s2jUnaryOperator(set => {
            if (set contains t) {
                throw new AlreadyRegisteredException("Service '" + t + "' already registered.")
            }
            set + t
        }))
    }

    def getRegistered: Set[T] = services.get()
}
