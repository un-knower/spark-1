package clworks.hive2es

import org.elasticsearch.spark._
import org.elasticsearch.spark.rdd.EsSpark
import org.elasticsearch.spark.rdd.Metadata._

import scala.collection.mutable

object ESRddRw {
  def main(args: Array[String]): Unit = {

    /**
      * 1.index可以理解为数据库；type理解为数据表；id相当于数据库表中记录的主键，是唯一的。
      * 2.spark:_index  docs:_type
      * 3.写入es时，rdd必须是Map或者case class结构
      * 4.读取es
      */

      //使用map方式
//    val numbers = Map("one" -> 1, "two" -> 2, "three" -> 3)
//    val airports = Map("arrival" -> "Otopeni", "SFO" -> "San Fran")
//    EsConfs.sc.makeRDD(Seq(numbers, airports)).saveToEs("spark/docs")

    //使用case class。EsSpark是个工具类
//    case class Trip(departure: String, arrival: String)
//    val upcomingTrip = Trip("OTP", "SFO")
//    val lastWeekTrip = Trip("MUC", "OTP")
//    val rdd = EsConfs.sc.makeRDD(Seq(upcomingTrip, lastWeekTrip))
//    EsSpark.saveToEs(rdd, "spark/docs")

    //写入json
//    val json1="""{"reason" : "business", "airport" : "SFO"}"""
//    val json2 = """{"participants" : 5, "airport" : "OTP"}"""
//    EsConfs.sc.makeRDD(Seq(json1, json2)).saveJsonToEs("spark/json-trips")

    //动态写入多种类型。{media_type}用于区分数据，但必须存在文档中
//    val game = Map("media_type"->"game","title" -> "FF VI","year" -> "1994")
//    val book = Map("media_type" -> "book","title" -> "Harry Potter","year" -> "2010")
//    val cd = Map("media_type" -> "music","title" -> "Surfing With The Alien")
//    EsConfs.sc.makeRDD(Seq(game, book, cd)).saveToEs("spark/{media_type}")

    //保存元数据。给文档指定id
 /*     val otp = Map("iata" -> "OTP", "name" -> "Otopeni")
    val muc = Map("iata" -> "MUC", "name" -> "Munich")
    val sfo = Map("iata" -> "SFO", "name" -> "San Fran")
    //传入的key-value pair RDD，key作为metadata，作为document ids，value作为document
    //EsConfs.sc.makeRDD(Seq((1, otp), (2, muc), (3, sfo))).saveToEsWithMeta("airports/2015")
    //设置meta
    val otpMeta = Map(ID -> 1, TTL -> "3h")
    val mucMeta = Map(ID -> 2, VERSION -> "23")
    val sfoMeta = Map(ID -> 3)
    //meta与doc组成的元组对
    EsConfs.sc.makeRDD(Seq((otpMeta, otp), (mucMeta, muc), (sfoMeta, sfo))).saveToEsWithMeta("airports/2015")
*/

    import EsConfs.hiveContext.implicits._

    //返回_id和内容，也可以指定查询query
    EsConfs.sc.esRDD("airports/2015","?q=me*").foreach(println(_))
    //返回json格式
    EsConfs.sc.esJsonRDD("airports/2015").foreach(println(_))

  }
}
