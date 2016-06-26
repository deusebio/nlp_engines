package com.intesasanpaolo.bip

import java.io.InputStream

package object opennlp {

  trait Tokens {val token: String}

  trait TaggedTokens extends Tokens {val tag: String}

  case class TaggedToken(token: String, tag: String) extends TaggedTokens {
    override def toString = token + ": " + tag
  }

  case class Token(token: String) extends Tokens {
    def tag(t: String): TaggedTokens = TaggedToken(token, t)
  }

  def readFileOpenNLP(namefile: String): InputStream = classOf[Token].getClassLoader().getResourceAsStream(namefile)

  def getResource(namefile: String) = classOf[Token].getClassLoader().getResource(namefile).getFile



}
