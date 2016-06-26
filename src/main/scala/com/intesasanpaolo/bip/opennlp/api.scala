package com.intesasanpaolo.bip.opennlp

import java.io.File

import com.intesasanpaolo.bip.opennlp.lemmatizers.SimpleLemmatizer
import opennlp.tools.cmdline.postag.POSModelLoader
import opennlp.tools.lemmatizer.DictionaryLemmatizer
import opennlp.tools.postag.{POSTaggerME, POSTagger}
import opennlp.tools.sentdetect.{SentenceModel, SentenceDetectorME, SentenceDetector}
import opennlp.tools.tokenize.{TokenizerModel, TokenizerME, Tokenizer}

class Document(val text: String, sentenceDetector: SentenceDetector = Defaults.sentenceDetector) {
  def sentences() = sentenceDetector.sentDetect(text).map(new Sentence(_))
}


class Sentence(val text: String,
               val tokenizer: Tokenizer = Defaults.tokenizer,
               val tagger: POSTagger = Defaults.tagger,
               val lemmatizer: DictionaryLemmatizer = Defaults.lemmatizer) {

  def tokens(): Array[Token] = tokenizer.tokenize(text).map(Token(_))

  def taggedTokens() =
    ( tokens zip tagger.tag(tokens().map(_.token)) ).map( p => TaggedToken(p._1.token, p._2) )

  def lemmas() = taggedTokens().map( token =>
    lemmatizer.lemmatize(token.token.toLowerCase, token.tag)
  )

}

object Defaults {

  private val defaultResourcePath = "./opennlp/models/"

  private val defaultSentenceDetectorFilename = "en-sent.bin"
  private val defaultTokenizerFilename = "en-token.bin"
  private val defaultPOSTaggerFilename = "en-pos-maxent.bin"
  private val defaultDictionaryLemmatizer = "lemmatizer-dicts/language-tool/en-lemmatizer.txt"


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

}
