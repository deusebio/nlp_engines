package com.intesasanpaolo.bip.scalanlp

object SerializableTasks {

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


/*
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
*/