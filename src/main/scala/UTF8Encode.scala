import java.io.{BufferedWriter, File, FileOutputStream, OutputStreamWriter}

import org.apache.commons.io.FileUtils
import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.hadoop.mapred.TextInputFormat
import org.apache.spark.sql.SparkSession

/**
  * 1.修改小文件编码
  * 2.spark读取gbk文件
  */
object UTF8Encode {
  def main(args: Array[String]): Unit = {
   val ss=SparkSession.builder().appName("encode").master("local[3]")
      .getOrCreate()
    import ss.implicits._

    //将特定文件格式转换为UTF-8
/*    val file=new File("data/MO_MSG.csv")
    val content=FileUtils.readFileToString(file,"gbk")
    FileUtils.write(file,content,"UTF-8")*/

    //spark读取特殊格式文件
    val encode=ss.sparkContext
      .hadoopFile("data/MO_MSG.csv", classOf[TextInputFormat], classOf[LongWritable], classOf[Text])
      .map(pair =>new String(pair._2.getBytes, 0, pair._2.getLength,"UTF-8"))

    val infile=""
    val outfile=new File("")
    val out=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outfile),"UTF-8"))



  }
}
