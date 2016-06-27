package com.intesasanpaolo.bip.scalanlp

import epic.parser.Parser
import epic.preprocess.{TreebankTokenizer, MLSentenceSegmenter}
import epic.sequences.{SemiCRF, CRF}
import epic.trees.AnnotatedLabel

object SerializableTasks {



}

object Defaults {

  val sentenceDetector: MLSentenceSegmenter = MLSentenceSegmenter.bundled().get

  val tokenizer: TreebankTokenizer = new epic.preprocess.TreebankTokenizer()

  val parser: Parser[AnnotatedLabel, String] = epic.models.ParserSelector.loadParser("en").get

  val tagger: CRF[AnnotatedLabel, String] = epic.models.PosTagSelector.loadTagger("en").get

  val nerTagger: SemiCRF[Any, String] = epic.models.NerSelector.loadNer("en").get
}

case class Document(text: String,
                    sentenceDetector:  MLSentenceSegmenter = Defaults.sentenceDetector) {
  def sentences() = sentenceDetector(text).map(Sentence(_))

}

case class Sentence(text: String,
                    tokenizer: TreebankTokenizer = Defaults.tokenizer,
                    parser: Parser[AnnotatedLabel, String] = Defaults.parser,
                    tagger: CRF[AnnotatedLabel, String] = Defaults.tagger,
                    nerTagger: SemiCRF[Any, String] = Defaults.nerTagger) {

  lazy val tokens = tokenizer(text)

  private def tree() = parser(tokens)
  def printTree = tree().render(tokens)

  def taggedTokens() = tagger.bestSequence(tokens)
  def printPOS() = taggedTokens().render

  def ner() = nerTagger.bestSequence(tokens)
  def printNER() = ner().render
}


object Test extends App {

  def textToLemmas(text: String) = {

    val sentenceSplitter: MLSentenceSegmenter = MLSentenceSegmenter.bundled().get
    val tokenizer: TreebankTokenizer = new epic.preprocess.TreebankTokenizer()

    val sentences: IndexedSeq[IndexedSeq[String]] = sentenceSplitter(text).map(tokenizer).toIndexedSeq

    val parser: Parser[AnnotatedLabel, String] = epic.models.ParserSelector.loadParser("en").get
    val tagger: CRF[AnnotatedLabel, String] = epic.models.PosTagSelector.loadTagger("en").get

    for (sentence <- sentences) {
      val tree = parser(sentence)
      println(tree.render(sentence))
      val tags = tagger.bestSequence(sentence)
    }

  }
}