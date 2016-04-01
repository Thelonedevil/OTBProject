package io.github.otbproject.otb.plugin.content

import io.github.otbproject.otb.core._
import io.github.otbproject.otb.plugin.content.data._

abstract class PluginDataFactory(plugin: ContentPlugin) {
  type ServicePluginData <: PluginData
  type ServiceBotPluginData <: PluginData
  type StaticBotPluginData <: PluginData
  type ServiceChannelPluginData <: PluginData
  type StaticChannelPluginData <: PluginData

  protected val serviceDataFactory: DataFactory[Service, ServicePluginData]
  protected val serviceBotDataFactory: DataFactory[ServiceBot, ServiceBotPluginData]
  protected val staticBotDataFactory: DataFactory[StaticBot, StaticBotPluginData]
  protected val serviceChannelDataFactory: DataFactory[ServiceChannel, ServiceChannelPluginData]
  protected val staticChannelDataFactory: DataFactory[StaticChannel, StaticChannelPluginData]

  final def getServiceData(data: ServiceData): ServicePluginData = serviceDataFactory.getData(data)

  final def getServiceBotData(data: ServiceBotData): ServiceBotPluginData = serviceBotDataFactory.getData(data)

  final def getStaticBotData(data: StaticBotData): StaticBotPluginData = staticBotDataFactory.getData(data)

  final def getServiceChannelData(data: ServiceChannelData): ServiceChannelPluginData =
    serviceChannelDataFactory.getData(data)

  final def getStaticChannelData(data: StaticChannelData): StaticChannelPluginData =
    staticChannelDataFactory.getData(data)

  private[content] final def serviceDF: DataFactory[Service, ServicePluginData] = serviceDataFactory

  private[content] final def serviceBotDF: DataFactory[ServiceBot, ServiceBotPluginData] = serviceBotDataFactory

  private[content] final def staticBotDF: DataFactory[StaticBot, StaticBotPluginData] = staticBotDataFactory

  private[content] final def serviceChannelDF: DataFactory[ServiceChannel, ServiceChannelPluginData] =
    serviceChannelDataFactory

  private[content] final def staticChannelDF: DataFactory[StaticChannel, StaticChannelPluginData] =
    staticChannelDataFactory
}

object EmptyPluginDataFactory extends PluginDataFactory(null) {
  override type ServicePluginData = PluginData
  override type ServiceBotPluginData = PluginData
  override type StaticBotPluginData = PluginData
  override type ServiceChannelPluginData = PluginData
  override type StaticChannelPluginData = PluginData

  override val serviceDataFactory: DataFactory[Service, PluginData] = DataFactory.empty
  override val staticBotDataFactory: DataFactory[StaticBot, PluginData] = DataFactory.empty
  override val serviceChannelDataFactory: DataFactory[ServiceChannel, PluginData] = DataFactory.empty
  override val serviceBotDataFactory: DataFactory[ServiceBot, PluginData] = DataFactory.empty
  override val staticChannelDataFactory: DataFactory[StaticChannel, PluginData] = DataFactory.empty
}
