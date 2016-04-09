package io.github.otbproject.otb.core.message.in

import io.github.otbproject.otb.core.ChannelUser

final case class MessageIn(source: MessageSource, user: ChannelUser, text: String) {
  private lazy val tokenizedText = text.split(" ").toStream.filterNot((s: String) => s.isEmpty).toList

  /**
    * Returns the text of the message split on spaces, with duplicate
    * spaces removed.
    *
    * @return a List of the text split on spaces
    */
  def getTokenizedText: List[String] = tokenizedText
}
