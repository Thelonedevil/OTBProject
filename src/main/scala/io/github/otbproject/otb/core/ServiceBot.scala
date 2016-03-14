package io.github.otbproject.otb.core

import io.github.otbproject.otb.core.data.ServiceBotData

trait ServiceBot {
    def getStatic: StaticBot

    def getInstanceData: ServiceBotData

    def getService: Service
}
