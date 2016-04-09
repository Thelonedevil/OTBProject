package io.github.otbproject.otb.core.message.in

import io.github.otbproject.otb.core.message.out.MessageDestination

trait MessageSource {
  def replyDestination: MessageDestination
}
