package com.intesasanpaolo.bip

import java.io.File
import java.util

import com.intesasanpaolo.bip.stanfordNLP.CoreNlpEntityExtractor.extractEntitiesFromText
import com.intesasanpaolo.bip.opennlp.{SerializableTasks => OpenNLPTasks}
import com.intesasanpaolo.bip.opennlp.{Document => OpenDocument}

import com.intesasanpaolo.bip.stanfordNLP.{serializableTasks => StanfordNLPTasks, NamedEntity}

import edu.stanford.nlp.simple.{Document => StanDocument, Sentence}

import com.github.tototoshi.csv._


object CoreNLPs extends App{

  implicit object MyFormat extends DefaultCSVFormat {
    override val delimiter = ';'
  }

  val reader = CSVReader.open( new File(getResourcePath("./docs/docs.csv")) )
  val tests = reader.allWithHeaders().toArray

  val ind = scala.util.Random.nextInt(tests.size)
  val test = tests(ind)("Text")
  // val test = "This is a try!"

  val openLemmas = OpenNLPTasks.textToLemmas(test)
  val stanLemmas = StanfordNLPTasks.textToLemmas(test)

  val differences = (openLemmas zip stanLemmas).zipWithIndex.filter({ case (p, ind) => p._1 != p._2 })

  /*
  println("OpenNLP vs StanfordNLP")
  println("-------")
  println(" ")
  differences.foreach(println)
  println(" ")
  */
  println("Original")
  println("-------")
  println(" ")
  println(test)
  println(" ")

  println("OpenNLP")
  println("-------")
  println(" ")
  println(openLemmas.mkString(", "))
  println(" ")

  println("StanfordNLP")
  println("-------")
  println(" ")
  println(stanLemmas.mkString(", "))
  println(" ")



}

object TestNER extends App {

  val test = "Barack Obama is my favorite hero after Mickey Mouse. The president of the United States is a joke. Bill Gates has had similar problems."

  val sDocument = new StanDocument(test)
  val sEntities: Seq[NamedEntity] = extractEntitiesFromText(test)

  val oDocument = new OpenDocument(test)
  val oEntities = oDocument.sentences().flatMap(_.ner()).toList

  oEntities.foreach(println)
  println("----------------")
  sEntities.foreach(println)

}



