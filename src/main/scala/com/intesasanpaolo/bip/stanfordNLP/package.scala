package com.intesasanpaolo.bip

import scala.collection.JavaConversions._
import edu.stanford.nlp.simple.Document


package object stanfordNLP {

  object serializableTasks extends Serializable {

    def textToLemmas(text: String): List[String] =
      (for (sentence <- new Document(text).sentences();
            lemma <- sentence.lemmas()) yield lemma.toLowerCase()
        ).toList

    def textToFilteredLemmas(text: String, stopWords: Set[String]): List[String] =
      (for (sentence <- new Document(text).sentences();
            lemma <- sentence.lemmas(); if !stopWords.contains(lemma.toLowerCase()))
        yield lemma.toLowerCase()
        ).toList

  }

}
