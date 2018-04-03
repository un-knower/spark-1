package clworks.hive2es

import org.elasticsearch.spark.sql._

case class Person(name: String, surname: String, age: String)


object ESsqlRw {
  def main(args: Array[String]): Unit = {

    /**
      * 1.默认使用case class关联es字段
      * 2.默认不导入null值，且不导入任何字段。需要设置属性es.spark.dataframe.write.null为true
      */
    import EsConfs.sqlContext.implicits._
    val people=EsConfs.sc.textFile("data/people.txt").map(_.split(","))
      .map(p=>Person(p(0),p(1),Some(p(2)).getOrElse("0"))).toDF() //不指定字段名时使用默认
    //people.saveToEs("spark/people")

    //读取es数据
    //EsConfs.sqlContext.read.format("es").load("spark/people").show()
    //EsConfs.sqlContext.read.format("org.elasticsearch.spark.sql").load("spark/people").show()
    EsConfs.sqlContext.read.format("es")
      //.options(Map("pushdown" -> "true"))
      //.load("spark/people").show()

    //注意es读取字段名称时需要注意大小写。当字段内容为空时，显示null值
    EsConfs.sqlContext.esDF("hive/test").filter($"coun".isNull).show()
  }

}
