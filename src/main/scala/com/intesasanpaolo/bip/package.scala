package com.intesasanpaolo

package object bip {

  class RefClass

  case class NamedEntity(name: String, category: String,  val uniqueIdentifier: String="")

  def getResourcePath(namefile: String) = classOf[RefClass].getClassLoader().getResource(namefile).getFile

}
