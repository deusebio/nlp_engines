package com.intesasanpaolo.bip.stanfordNLP

import scala.collection.JavaConversions._
import edu.stanford.nlp.simple.{Document, Sentence}

import scalaz.NonEmptyList
import scalaz.syntax.std.ListOps

object CoreNlpEntityExtractor {

  val allRegexp = List("""\s*,\s*""".r)

  val entityTypes = Set("ORGANIZATION", "LOCATION", "PERSON", "DATE")

  /**
   * given a sentence extracts all entities.
   *
   */
  def extractEntitiesFromSentence(sentence: Sentence): List[(String, String)] = {

    val nerTags = sentence.nerTags()

    val entitiesTypeAndLemmasIndices: List[(String, Int)] = nerTags
      .zipWithIndex
      .filter(!_._1.equals("O"))
      .toList

    // group contiguous entities pieces
    val grouped: List[NonEmptyList[(String, Int)]] =
      new ListOps[(String, Int)](entitiesTypeAndLemmasIndices).groupWhen((previous, current)=>{
        (
          // type matches: ex PERSON = PERSON
          previous._1.equals(current._1) &&
            // they are contiguous words, so that we know there are no holes between words
            (previous._2 + 1).equals(current._2) &&
            // is a non empty lemma
            allRegexp.forall(m => m.findAllIn(sentence.lemma(current._2)).isEmpty)
          )
      })

    /**
     * it is something like:
     * List[ List[("PERSON",1),("PERSON",2)],List[("LOCATION",5)]
     *                     Bob          Marley               Jamaica
     */
    grouped.map(x => (x.head._1, {
      val f: NonEmptyList[String] = x.map(_._2).map(sentence.lemma(_))
      (f.head :: f.tail).toList.mkString(" ")
    } ))
  }

  /**
   *
   */
  def extractEntitiesFromText(text: String): Seq[NamedEntity] = {
    val extracted = new Document(text).sentences.map(extractEntitiesFromSentence)

    extracted.filter(!_.isEmpty).flatten.map(x => {
      NamedEntity(name = x._2, category = x._1)
    })
  }
}
