package clworks

import com.hankcs.hanlp.HanLP
import org.ansj.splitWord.analysis.ToAnalysis

import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer

class ChineSeg {

  def runAnsjSplitArr(text:String):Array[String]={
    val parse=ToAnalysis.parse(text)
    val terms=parse.getTerms
    val arr=new ArrayBuffer[String]()
    for(i<-0 to terms.size()-1){
      val word=terms(i).getName
      arr+=word
    }
    arr.toArray
  }


  def runAnsjSplit(text:String):String={
    val parse=ToAnalysis.parse(text)
    val terms=parse.getTerms
    val sb=new StringBuilder
    for(i<-0 to terms.size()-1){
      //去除人名
      if(! terms(i).getNatureStr.equals("nr")){
        val word=terms(i).getName
        sb.append(word+",")
      }
    }
    sb.toString().substring(0,sb.toString().length)
  }

  def runAnsjSplitNSArr(text:String):Array[String]={
    val parse=ToAnalysis.parse(text)
    val terms=parse.getTerms
    val arr=new ArrayBuffer[String]()
    for(i<-0 to terms.size()-1){
      if(! terms(i).getNatureStr.equals("nr")){
        val word=terms(i).getName
        arr+=word
      }
    }
    arr.toArray
  }


}
