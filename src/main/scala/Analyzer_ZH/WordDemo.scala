package Analyzer_ZH

import org.apdplat.word.WordSegmenter

object WordDemo {
  def main(args: Array[String]): Unit = {
    val text="东风里，朱门映柳，低按小秦筝。"

    //移除停用词
    val wordsWithoutStop=WordSegmenter.seg(text)
    //保留停用词
    val wordsWithStop=WordSegmenter.segWithStopWords(text)
    println(wordsWithStop)
    println(wordsWithoutStop)

    for(i<-0 to wordsWithStop.size()-1) {
      val word = wordsWithStop.get(i).getText
      println(word)
    }
  }
}
