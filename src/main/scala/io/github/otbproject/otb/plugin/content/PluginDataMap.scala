package io.github.otbproject.otb.plugin.content

import scala.collection.mutable

private[content] final class PluginDataMap private(map: Map[PluginDataTypeIdentifier[_], _ <: PluginData]) {
  private val dataMap: Map[PluginDataTypeIdentifier[_], _ <: PluginData] = map

  def get[T <: PluginData](identifier: PluginDataTypeIdentifier[T]): T = {
    // Cast never fails because it is constrained that a type T object only gets
    // inserted with a PluginDataTypeIdentifier[T] as its key
    identifier.tClass.cast(dataMap.get(identifier))
  }
}

private[content] object PluginDataMap {
  def newBuilder: Builder = new Builder

  final class Builder {
    private val mutableMap = new mutable.HashMap[PluginDataTypeIdentifier[_], PluginData]()

    @throws[IllegalArgumentException]
    def put[T <: PluginData](identifier: PluginDataTypeIdentifier[T], data: T) = {
      if (mutableMap contains identifier) {
        throw new IllegalArgumentException("Plugin-Class mapping already present: " + identifier)
      }
      mutableMap.put(identifier, data)
    }

    def build: PluginDataMap = new PluginDataMap(mutableMap.toMap)
  }

}


