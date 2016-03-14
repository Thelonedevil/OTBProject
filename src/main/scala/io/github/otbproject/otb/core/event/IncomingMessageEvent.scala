package io.github.otbproject.otb.core.event

import io.github.otbproject.otb.core.message.in.MessageIn

final case class IncomingMessageEvent(message: MessageIn)