package breeze

import epic.models.{ParserSelector, PosTagSelector}
import epic.preprocess.{MLSentenceSegmenter, TreebankTokenizer}

object EpicDemo {
  def main(args: Array[String]): Unit = {

    /**
      * 注意：在使用所有模型前，都需要先分句和分词
      */

    /**
      * Preprocessing text 处理文本
      * 1.MLSentenceSegmenter将文本分割成句子
      * 2.TreebankTokenizer将句子分割成一系列的词
      * 3.use the pipeline
      */

    val text="I don't deserve this, you look perfect tonight!"
    val sentenceSplitter=MLSentenceSegmenter.bundled().get
    val tokenizer=new TreebankTokenizer()
    val sentences=sentenceSplitter(text).map(tokenizer).toIndexedSeq

/*    for(sentence<-sentences){
      //Vector(I, do, n't, deserve, this, ,, you, look, perfect, tonight, !)
      println(sentence)
    }*/

    /**
      * Parser 句法解析树
      *
      */
    val parser=ParserSelector.loadParser("en").get

    for(sentence<-sentences) {
      println(sentence)
      val tree=parser(sentence)
      println(tree.render(sentence))
    }

/*    val tagger=PosTagSelector.loadTagger("en").get
    for(sentence<-sentences) {
      println(sentence)
      val tags=tagger.bestSequence(sentence)
      println(tags.render)
    }*/

    }
}
