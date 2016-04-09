package io.github.otbproject.otb.core.message.out

trait MessageDestination {
  def sendMessage(message: MessageOut): Boolean
}
