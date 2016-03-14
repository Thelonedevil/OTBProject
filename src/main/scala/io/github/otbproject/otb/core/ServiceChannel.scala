package io.github.otbproject.otb.core

import io.github.otbproject.otb.core.data.StaticChannelData
import io.github.otbproject.otb.core.fs.FileSystemObject

trait ServiceChannel extends FileSystemObject {
    def getStaticData: StaticChannelData

    def getBot: ServiceBot

    def getUser(name: String): ChannelUser
}
