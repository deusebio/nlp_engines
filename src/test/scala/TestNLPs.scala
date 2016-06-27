package com.intesasanpaolo.bip

import java.io.File

import com.intesasanpaolo.bip.stanfordNLP.CoreNlpEntityExtractor.extractEntitiesFromText

// SerializableTasks
import com.intesasanpaolo.bip.opennlp.{SerializableTasks => OpenNLPTasks}
import com.intesasanpaolo.bip.stanfordNLP.{serializableTasks => StanfordNLPTasks}
import com.intesasanpaolo.bip.scalanlp.{SerializableTasks => ScalaNLPTasks}

// Documents types
import edu.stanford.nlp.simple.{Document => StanDocument}
import com.intesasanpaolo.bip.scalanlp.{Document => ScDocument, Sentence}
import com.intesasanpaolo.bip.opennlp.{Document => OpenDocument}

import com.github.tototoshi.csv._

object CoreNLPs extends App {

  implicit object MyFormat extends DefaultCSVFormat {
    override val delimiter = ';'
  }

  val reader = CSVReader.open(new File(getResourcePath("./docs/docs.csv")))
  val tests = reader.allWithHeaders().toArray

  val ind = scala.util.Random.nextInt(tests.size)
  val test = tests(ind)("Text")
  // val test = "This is a try!"

  def timing(f: => List[String], label: String) = {
    val t0 = System.nanoTime()
    println(label)
    val res = f
    println("Execution time: " + ((System.nanoTime() - t0) / 1E9))
    println(" ")
    res
  }

  val openLemmas = timing(OpenNLPTasks.textToLemmas(test), "OpenNLP")
  val stanLemmas = timing(StanfordNLPTasks.textToLemmas(test), "StanfordNLP")
  val scalLemmas = timing(ScalaNLPTasks.textToLemmas(test), "ScalaNLP")

  val differences1 = (openLemmas zip stanLemmas).zipWithIndex.filter({ case (p, ind) => p._1 != p._2 })
  val differences2 = (scalLemmas zip stanLemmas).zipWithIndex.filter({ case (p, ind) => p._1 != p._2 })

  /*
  println("OpenNLP vs StanfordNLP")
  println("-------")
  println(" ")
  differences.foreach(println)
  println(" ")
  */

  println("Results")

  def printRes(string: String, label: String) = {
    println(label)
    println("-------")
    println(" ")
    println(string)
    println(" ")
  }

  printRes(test, "Original")
  printRes(openLemmas.mkString(", "), "OpenNLP")
  printRes(scalLemmas.mkString(", "), "ScalaNLP")
  printRes(stanLemmas.mkString(", "), "StanfordNLP")

}


object TestNER extends App {

  val test = "Barack Obama is my favorite hero after Mickey Mouse. The president of the United States is a joke. Bill Gates has had similar problems."

  println("Stanford")
  val ts = System.nanoTime()
  val sDocument = new StanDocument(test)
  val sEntities: Seq[NamedEntity] = extractEntitiesFromText(test)
  println(" Execution Time: " + ( (System.nanoTime()-ts) / 1E9 ) )
  println("")
  oEntities.foreach(println)
  println("----------------")

  println("OpenNLP")
  val to = System.nanoTime()
  val oDocument = new OpenDocument(test)
  val oEntities: List[NamedEntity] = oDocument.sentences().flatMap(_.ner()).toList
  println(" Execution Time: " + ( (System.nanoTime()-ts) / 1E9 ) )
  println("")
  sEntities.foreach(println)
  println("----------------")

  println("ScalaNLP")
  val tsc = System.nanoTime()
  val scDocument = new ScDocument(test)
  val scEntities: IndexedSeq[NamedEntity] = scDocument.sentences().flatMap(_.ner())
  println(" Execution Time: " + ( (System.nanoTime()-tsc) / 1E9 ) )
  println("")
  scEntities.foreach(println)
  println("----------------")
}



