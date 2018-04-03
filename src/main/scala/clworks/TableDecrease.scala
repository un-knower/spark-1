package clworks

import org.apache.log4j.{Level, Logger}

object TableDecrease {
  def main(args: Array[String]): Unit = {

    Logger.getLogger("org").setLevel(Level.ERROR)

    import Utils.hiveContext.implicits._

    val oriDF=Utils.getDF("SENSWORD_COUNT_2017_1").select("CONTENT")
    //只留下中文字符，并将人名去除
    val filter=oriDF
      .map(i=>(i(0).toString.replaceAll("[^\\u4e00-\\u9fa5]","")))
      .filter(_.length>1)
      .map(i=>(new ChineSeg().runAnsjSplit(i)))
      .toDF("CONTENT")

    //3279-->1830
    println(filter.distinct().count())


  }
}
