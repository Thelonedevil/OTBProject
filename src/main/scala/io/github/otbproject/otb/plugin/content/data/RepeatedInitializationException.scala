package io.github.otbproject.otb.plugin.content.data

final class RepeatedInitializationException private[content](data: Data[_])
  extends IllegalStateException("Attempted to generate PluginData for [" + data + "] more than once. " +
                                  "This was probably caused by some plugin attempting to access data from the object " +
                                  "during its initialization.")
