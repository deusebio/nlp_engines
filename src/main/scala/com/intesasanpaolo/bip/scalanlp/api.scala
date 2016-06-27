package com.intesasanpaolo.bip.scalanlp

import com.intesasanpaolo.bip.NamedEntity
import epic.features.PorterStemmer
import epic.parser.Parser
import epic.preprocess.{TreebankTokenizer, MLSentenceSegmenter}
import epic.sequences.{Segmentation, SemiCRF, CRF}
import epic.trees.AnnotatedLabel

object Defaults {

  lazy val sentenceDetector: MLSentenceSegmenter = MLSentenceSegmenter.bundled().get

  lazy val tokenizer: TreebankTokenizer = new epic.preprocess.TreebankTokenizer()

  lazy val parser: Parser[AnnotatedLabel, String] = epic.models.ParserSelector.loadParser("en").get

  lazy val tagger: CRF[AnnotatedLabel, String] = epic.models.PosTagSelector.loadTagger("en").get

  lazy val nerTagger: SemiCRF[Any, String] = epic.models.NerSelector.loadNer("en").get
    .asInstanceOf[SemiCRF[Any, String]]
}

class Document(text: String,
               sentenceDetector:  MLSentenceSegmenter = Defaults.sentenceDetector) {
  def sentences() = sentenceDetector(text).map(new Sentence(_))

}

// case class Entity(val kind: String, val token: String, val score: Double)

class Sentence(text: String,
               tokenizer: TreebankTokenizer = Defaults.tokenizer,
               parser: Parser[AnnotatedLabel, String] = Defaults.parser,
               tagger: CRF[AnnotatedLabel, String] = Defaults.tagger,
               nerTagger: SemiCRF[Any, String] = Defaults.nerTagger) {

  lazy val tokens = tokenizer(text)

  def lemmas() = tokens.map(PorterStemmer(_))

  private def tree() = parser(tokens)
  def printTree = tree().render(tokens)

  def taggedTokens() = tagger.bestSequence(tokens)
  def printPOS() = taggedTokens().render

  def rawNER() = nerTagger.bestSequence(tokens)
  def printNER() = rawNER().render

  def ner() = {
    val raw = rawNER()
    val wordList = rawNER().features
    raw.label.map({ case (en, span) =>
      NamedEntity(wordList.slice(span.begin, span.end).mkString(" "), en.toString())
    })
  }
}

