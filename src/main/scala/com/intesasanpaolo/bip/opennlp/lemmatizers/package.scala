package com.intesasanpaolo.bip.opennlp

package object lemmatizers {

  type LemmasBinding = Map[String, String]

  type LetterBinding = Map[Char, LemmasBinding]

  type TypeBinding = Map[String, LetterBinding]

}
