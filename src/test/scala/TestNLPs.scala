package com.intesasanpaolo.bip

import java.io.File

import opennlp.{SerializableTasks => OpenNLPTasks}
import stanfordNLP.{serializableTasks => StanfordNLPTasks}

import com.github.tototoshi.csv._


object TestNLPs extends App{

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
