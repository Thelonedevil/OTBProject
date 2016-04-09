package io.github.otbproject.otb.core

import io.github.otbproject.otb.misc.CredentialPair
import io.github.otbproject.otb.plugin.content.data.ServiceData

trait Service {
  def name: String

  def createBot(credentialPair: CredentialPair): ServiceBot

  def getData: ServiceData
}
