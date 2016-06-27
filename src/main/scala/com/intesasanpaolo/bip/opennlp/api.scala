package com.intesasanpaolo.bip.opennlp

import java.io.File

import com.intesasanpaolo.bip.opennlp.lemmatizers.SimpleLemmatizer
import opennlp.tools.cmdline.postag.POSModelLoader
import opennlp.tools.lemmatizer.DictionaryLemmatizer
import opennlp.tools.namefind.{NameFinderME, TokenNameFinderModel}
import opennlp.tools.postag.{POSTaggerME, POSTagger}
import opennlp.tools.sentdetect.{SentenceModel, SentenceDetectorME, SentenceDetector}
import opennlp.tools.tokenize.{TokenizerModel, TokenizerME, Tokenizer}
import opennlp.tools.util.Span

class Document(val text: String, sentenceDetector: SentenceDetector = Defaults.sentenceDetector,
               val nerModels: Map[String, TokenNameFinderModel] = Defaults.nerModels) {
  implicit val nerTagger = nerModels.mapValues(new NameFinderME(_))
  def sentences() = sentenceDetector.sentDetect(text).map(new Sentence(_))
}

case class Entity(val kind: String, val token: String, val score: Double)

class Sentence(val text: String,
               val tokenizer: Tokenizer = Defaults.tokenizer,
               val tagger: POSTagger = Defaults.tagger,
               val lemmatizer: DictionaryLemmatizer = Defaults.lemmatizer)(implicit val nerTagger: Map[String, NameFinderME]) {

  def words(): Array[String] = tokenizer.tokenize(text)

  def tokens(): Array[Token] = words().map(Token(_))

  def taggedTokens() =
    ( tokens zip tagger.tag(tokens().map(_.token)) ).map( p => TaggedToken(p._1.token, p._2) )

  def lemmas() = taggedTokens().map( token =>
    lemmatizer.lemmatize(token.token.toLowerCase, token.tag)
  )

  def ner(): List[Entity] = {
    val wordList: Array[String] = words()
    for ( typeNER <- nerTagger.mapValues( _.find(wordList)).toList;
          span <- typeNER._2
    ) yield Entity(span.getType, wordList.slice(span.getStart(), span.getEnd()).mkString(" "), span.getProb())

  }

}

object Defaults {

  private val defaultResourcePath = "./opennlp/models/"

  private val defaultSentenceDetectorFilename = "en-sent.bin"
  private val defaultTokenizerFilename = "en-token.bin"
  private val defaultPOSTaggerFilename = "en-pos-maxent.bin"
  private val defaultDictionaryLemmatizer = "lemmatizer-dicts/language-tool/en-lemmatizer.txt"

  private val defaultNERFilenames = Map(
    "date" -> "ner/en-ner-date.bin",
    "location" -> "ner/en-ner-location.bin",
    "money" -> "ner/en-ner-money.bin",
    "organization" -> "ner/en-ner-organization.bin",
    "percentage" -> "ner/en-ner-percentage.bin",
    "person" -> "ner/en-ner-person.bin",
    "time" -> "ner/en-ner-time.bin"
  )

  val sentenceDetector = new SentenceDetectorME(
    new SentenceModel(readFileOpenNLP(defaultResourcePath + defaultSentenceDetectorFilename))
  )

  val tokenizer: Tokenizer = new TokenizerME(
    new TokenizerModel(readFileOpenNLP(defaultResourcePath + defaultTokenizerFilename))
  )

  val tagger: POSTaggerME = new POSTaggerME(
    new POSModelLoader().load(new File(getResource(defaultResourcePath + defaultPOSTaggerFilename)))
  )

  val lemmatizer = SimpleLemmatizer.fit(defaultResourcePath + defaultDictionaryLemmatizer)

  val nerModels = defaultNERFilenames.mapValues({ case filename =>
    new TokenNameFinderModel(readFileOpenNLP(defaultResourcePath + filename))
  })

}
