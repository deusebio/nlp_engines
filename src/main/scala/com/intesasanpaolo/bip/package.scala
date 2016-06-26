package com.intesasanpaolo

package object bip {

  class RefClass

  def getResourcePath(namefile: String) = classOf[RefClass].getClassLoader().getResource(namefile).getFile

}
