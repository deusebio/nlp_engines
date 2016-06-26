package com.intesasanpaolo.bip.opennlp.lemmatizers

import com.intesasanpaolo.bip.logging.Logging
import com.intesasanpaolo.bip.opennlp._
import opennlp.tools.lemmatizer.DictionaryLemmatizer

import scala.io.Source

object SimpleLemmatizer extends Logging  {

  def rawInput(dictionary: String) = Source.fromFile(getResource(dictionary))
    .getLines().map(_.split("\t"))

  def fit(dictionary: String): SimpleLemmatizerModel = {

    logger.info(" Lemmatizer: Loading dictionary " + dictionary)

    val input = rawInput(dictionary).toList
    val types = input.foldLeft(Map.empty[String, LetterBinding])({ case (agg, item) =>

      val itemMap: LemmasBinding = Map(item(0) -> item(1))

      agg.updated(
        item(2), agg.get(item(2)) match {
          case Some(i) => i.updated( item(0)(0),
            i.get(item(0)(0)) match {
              case Some(j) => j ++ itemMap
              case None => itemMap
            })
          case None => Map(item(0)(0) -> itemMap)
        })
    })
    logger.info(" Lemmatizer: ...DONE! ")

    new SimpleLemmatizerModel(types)
  }
}

class SimpleLemmatizerModel(val map: TypeBinding) extends DictionaryLemmatizer  {

  def lemmatize(word: String, tag: String): String =
    ( for( t <- map.get(tag); w <- t.get(word(0)) )
      yield {w.getOrElse(word, word)}
      ).getOrElse(word)

  def lemmatize(token: TaggedToken): String = lemmatize(token.token, token.tag)

  def transform(tokens: Array[TaggedToken]): Array[String] = tokens.map(lemmatize)

}
