package io.github.otbproject.otb.plugin.content

import io.github.otbproject.otb.core._
import io.github.otbproject.otb.plugin.content.data._

abstract class PluginDataFactory(plugin: ContentPlugin) {
    type ServiceDataType <: PluginData
    type ServiceBotDataType <: PluginData
    type StaticBotDataType <: PluginData
    type ServiceChannelDataType <: PluginData
    type StaticChannelDataType <: PluginData

    protected val serviceDataFactory: DataFactory[Service, ServiceDataType]
    protected val serviceBotDataFactory: DataFactory[ServiceBot, ServiceBotDataType]
    protected val staticBotDataFactory: DataFactory[StaticBot, StaticBotDataType]
    protected val serviceChannelDataFactory: DataFactory[ServiceChannel, ServiceChannelDataType]
    protected val staticChannelDataFactory: DataFactory[StaticChannel, StaticChannelDataType]

    final def getServiceData(data: ServiceData): ServiceDataType = serviceDataFactory.getData(data)

    final def getServiceBotData(data: ServiceBotData): ServiceBotDataType = serviceBotDataFactory.getData(data)

    final def getStaticBotData(data: StaticBotData): StaticBotDataType = staticBotDataFactory.getData(data)

    final def getServiceChannelData(data: ServiceChannelData): ServiceChannelDataType = serviceChannelDataFactory.getData(data)

    final def getStaticChannelData(data: StaticChannelData): StaticChannelDataType = staticChannelDataFactory.getData(data)

    private[plugin] final def getServiceDataFactory: DataFactory[Service, ServiceDataType] = serviceDataFactory

    private[plugin] final def getServiceBotDataFactory: DataFactory[ServiceBot, ServiceBotDataType] = serviceBotDataFactory

    private[plugin] final def getStaticBotDataFactory: DataFactory[StaticBot, StaticBotDataType] = staticBotDataFactory

    private[plugin] final def getServiceChannelDataFactory: DataFactory[ServiceChannel, ServiceChannelDataType] = serviceChannelDataFactory

    private[plugin] final def getStaticChannelDataFactory: DataFactory[StaticChannel, StaticChannelDataType] = staticChannelDataFactory
}

object EmptyPluginDataFactory extends PluginDataFactory(null) {
    override type ServiceDataType = PluginData
    override type ServiceBotDataType = PluginData
    override type StaticBotDataType = PluginData
    override type ServiceChannelDataType = PluginData
    override type StaticChannelDataType = PluginData

    override val serviceDataFactory: DataFactory[Service, PluginData] = DataFactory.empty
    override val staticBotDataFactory: DataFactory[StaticBot, PluginData] = DataFactory.empty
    override val serviceChannelDataFactory: DataFactory[ServiceChannel, PluginData] = DataFactory.empty
    override val serviceBotDataFactory: DataFactory[ServiceBot, PluginData] = DataFactory.empty
    override val staticChannelDataFactory: DataFactory[StaticChannel, PluginData] = DataFactory.empty
}
