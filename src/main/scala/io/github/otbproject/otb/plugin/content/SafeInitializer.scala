package io.github.otbproject.otb.plugin.content

import io.github.otbproject.otb.misc.LambdaUtil

private[content] final class SafeInitializer[T, P <: PluginData](initializer: T => P) {
    val state: ThreadLocal[State] =
        ThreadLocal.withInitial(LambdaUtil.s2jSupplier(() => READY))

    def reset() = {
        state.set(READY)
    }

    @throws[InitializationLoopException]
    def apply(t: T): P = {
        state.get() match {
            case READY =>
                state.set(INITIALIZING)
                val p: P = initializer(t)
                state.set(FINISHED)
                p
            case INITIALIZING => throw new InitializationLoopException(t, initializer)
            case FINISHED => throw new RepeatedInitializationException(t)
        }
    }

    trait State

    private object READY extends State

    private object INITIALIZING extends State

    private object FINISHED extends State

}

final class InitializationLoopException private[content](any: Any, func: _ => _)
    extends IllegalStateException("Loop occurred when attempting to generate PluginData " +
        "from: [" + any + "] using: [" + func + "]. This is caused by [" + func +
        "] attempting to access data from the object during its initialization.")

final class RepeatedInitializationException private[content](any: Any)
    extends IllegalStateException("Attempted to generate PluginData from [" + any + "] more than once. " +
        "This is caused by another plugin attempting to access data from the object during its initialization. " +
        "Look for an " + classOf[InitializationLoopException].getSimpleName + " to find the cause.")