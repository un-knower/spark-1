package Stanfordcorenlp.SimpleCoreNLP

import edu.stanford.nlp.ie.machinereading.structure.Span
import edu.stanford.nlp.simple._

object StanfordSimpleNlpExample {
  def main(args: Array[String]): Unit = {

    //Sentence应用于单个句子
    val sentence=new Sentence("Lucy is in the sky with diamonds.")

    println(sentence.openie())
    //ner
    val nertags=sentence.nerTags()
    /*    for(i<-0 to nertags.size()-1){
      println(nertags.get(i))   [PERSON, O, O, O, O, O, O, O]
    }*/

    //pos
    val posTag=sentence.posTags()
/*    for(i<-0 to posTag.size()-1){
      println(posTag.get(i))  [NNP,VBZ,IN,DT ,NN ,IN ,NNS,.]
    }*/


    //Document应用于多个语句
    val doc=new Document("add your text here! It can contain multiple sentences.")
    //返回包含每个句子的List
    val sent=doc.sentences()
/*    for(i<-0 to sent.size()-1){
      println("The word of the sentence '" + sent.get(i) + "' is " + sent.get(i).words())
      println("The lemma of the sentence '" + sent.get(i) + "' is " + sent.get(i).lemmas())
      println("The of the sentence '" + sent.get(i) + "' is " + sent.get(i).parse())
    }*/

    println(sentence.algorithms().headOfSpan(new Span(0,3)))   //1
  }
}
