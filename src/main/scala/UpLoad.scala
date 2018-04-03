import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.hadoop.io.compress.{DefaultCodec, GzipCodec, Lz4Codec}
import org.apache.hadoop.mapred.TextInputFormat
import org.apache.spark.sql.SparkSession

object UpLoad {
  def main(args: Array[String]): Unit = {
    val ss=SparkSession.builder().appName("test").master("local[3]")
      .getOrCreate()

    import ss.implicits._


/*
    val file=ss.sparkContext.hadoopFile("data/20180121/00_http.zip",
      classOf[TextInputFormat],classOf[LongWritable],classOf[Text]
    ).map(pair => new String(pair._2.getBytes, 0, pair._2.getLength, "utf-8"))
    file.foreach(println(_))
*/

    val file=ss.sparkContext.textFile("data/20180121/00_http.zip")
    file.saveAsTextFile("hdfs://172.16.20.30:9000/user/zhiqun")

/*    val file=ss.sparkContext.textFile("data/labels.txt").coalesce(1,true)
    file.saveAsTextFile("data/upload")*/

/*    val file=ss.sparkContext.textFile("data/upload/part-00000")
    file.map(i=>{
      new String(i.getBytes("utf-8"),"utf-8")
    }).foreach(println(_))*/


  }
}
