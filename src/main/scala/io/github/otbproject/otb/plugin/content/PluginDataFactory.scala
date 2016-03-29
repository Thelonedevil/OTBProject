package io.github.otbproject.otb.plugin.content

import io.github.otbproject.otb.core._
import io.github.otbproject.otb.plugin.content.data._

abstract class PluginDataFactory[A <: PluginData, B <: PluginData, C <: PluginData, D <: PluginData, E <: PluginData](plugin: ContentPlugin[_]) {
    protected val serviceDataFactory: DataFactory[Service, A]

    protected val serviceBotDataFactory: DataFactory[ServiceBot, B]

    protected val staticBotDataFactory: DataFactory[StaticBot, C]

    protected val serviceChannelDataFactory: DataFactory[ServiceChannel, D]

    protected val staticChannelDataFactory: DataFactory[StaticChannel, E]

    final def getServiceData(data: ServiceData): A = serviceDataFactory.getData(data)

    final def getServiceBotData(data: ServiceBotData): B = serviceBotDataFactory.getData(data)

    final def getStaticBotData(data: StaticBotData): C = staticBotDataFactory.getData(data)

    final def getServiceChannelData(data: ServiceChannelData): D = serviceChannelDataFactory.getData(data)

    final def getStaticChannelData(data: StaticChannelData): E = staticChannelDataFactory.getData(data)

    private[plugin] final def getServiceDataFactory: DataFactory[Service, A] = serviceDataFactory

    private[plugin] final def getServiceBotDataFactory: DataFactory[ServiceBot, B] = serviceBotDataFactory

    private[plugin] final def getStaticBotDataFactory: DataFactory[StaticBot, C] = staticBotDataFactory

    private[plugin] final def getServiceChannelDataFactory: DataFactory[ServiceChannel, D] = serviceChannelDataFactory

    private[plugin] final def getStaticChannelDataFactory: DataFactory[StaticChannel, E] = staticChannelDataFactory
}

object EmptyPluginDataFactory extends PluginDataFactory[PluginData, PluginData, PluginData, PluginData, PluginData](null) {
    override val serviceDataFactory: DataFactory[Service, PluginData] = DataFactory.empty
    override val staticBotDataFactory: DataFactory[StaticBot, PluginData] = DataFactory.empty
    override val serviceChannelDataFactory: DataFactory[ServiceChannel, PluginData] = DataFactory.empty
    override val serviceBotDataFactory: DataFactory[ServiceBot, PluginData] = DataFactory.empty
    override val staticChannelDataFactory: DataFactory[StaticChannel, PluginData] = DataFactory.empty
}
