package io.github.otbproject.otb.core

import io.github.otbproject.otb.core.fs.FileSystemObject

trait ServiceChannel extends FileSystemObject {
    def getStatic: StaticChannel

    def getBot: ServiceBot

    def getUser(name: String): ChannelUser
}
