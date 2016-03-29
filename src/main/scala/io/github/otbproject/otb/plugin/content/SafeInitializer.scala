package io.github.otbproject.otb.plugin.content

private[content] final class SafeInitializer[T, P <: PluginData](initializer: T => P) {
    var initializing: ThreadLocal[Boolean] = new ThreadLocal
    initializing.set(false)

    @throws[InitializationLoopException]
    def apply(t: T): P = {
        if (initializing.get()) {
            throw new InitializationLoopException(t, initializer)
        }
        initializing.set(true)
        val p: P = initializer(t)
        initializing.set(false)
        p
    }
}

final class InitializationLoopException private[content](any: Any, func: _ => _)
    extends IllegalStateException("Loop occurred when attempting to generate PluginData "
        + "from: [" + any + "] using: [" + func + "]")
