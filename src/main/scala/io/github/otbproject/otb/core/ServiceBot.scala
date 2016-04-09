package io.github.otbproject.otb.core

import io.github.otbproject.otb.plugin.content.data.ServiceBotData

trait ServiceBot {
  def getStatic: StaticBot

  def getInstanceData: ServiceBotData

  def getService: Service
}
