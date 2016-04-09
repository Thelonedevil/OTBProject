package io.github.otbproject.otb.plugin.base

import com.google.common.eventbus.EventBus
import org.apache.logging.log4j.Logger

abstract class Plugin private[plugin](initializer: PluginInitializer) {
  type Info <: PluginInfo

  val info: Info

  /**
    * The [[EventBus]] which is used to broadcast and subscribe to all
    * built-in events.
    *
    * All plugins have the same EventBus instance; this method is merely for
    * convenience.
    */
  final val eventBus: EventBus = initializer.eventBus

  /**
    * The [[Logger]] specific to this plugin.
    *
    * It is recommended to create an accessor method (e.g. 'logger()') which
    * is private to the scope of the plugin (can only be called from the
    * plugin's code, but not from outside it).
    */
  protected final val pluginLogger: Logger = initializer.logger

  // TODO: throw exception
  final def getRequiredDependencyInstance[P <: Plugin](dependency: Dependency[P]): P = {
    ??? // TODO: impl
  }

  final def getOptionalDependencyInstance[P <: Plugin](dependency: Dependency[P]): Option[P] = {
    ??? // TODO: impl
  }
}
