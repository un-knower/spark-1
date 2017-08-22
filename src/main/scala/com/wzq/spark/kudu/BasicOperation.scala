package com.wzq.spark.kudu

import org.apache.kudu.spark.kudu._
import org.apache.kudu.client._
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{Row, SQLContext, SparkSession}
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import collection.JavaConverters._

case class BasicOperation(name:String, age:Int, city:String)
object BasicOperation {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("kudu").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ss = SparkSession.builder().getOrCreate()
    /**
      * 创建kuduContext
      * 创建kudu表的5个步骤
      * 1.表名
      * 2.表的结构schema
      * 3.主键
      * 4.定义表主要参数，比如分区
      * 5.调用创建表的方法
      */
    val master1 = "1:7051"
    val master2 = "2:7051"
    val master3 = "3:7051"
    val kuduMasters = Seq(master1, master2, master3).mkString(",")
    val kuduContext = new KuduContext(kuduMasters, sc)
    val kuduTableName = "spark_kudu_tbl"
    if (kuduContext.tableExists(kuduTableName)) {
      kuduContext.deleteTable(kuduTableName)
    }
    val kuduTableSchema = StructType(
      //    column name   type   nullable
      Array(StructField("name", StringType, false), StructField("age", IntegerType, true), StructField("city", StringType, true))
    )
    val kuduTableSchema2 = StructType(
      StructField("name", StringType, false) :: StructField("age", IntegerType, true) :: StructField("city", StringType, true) :: Nil
    )
    val kuduPrimaryKey = Seq("name")
    val kuduTableOptions = new CreateTableOptions()
    //asJava:因为这里需要的是java的List
    kuduTableOptions.setRangePartitionColumns(List("name").asJava).setNumReplicas(1)
    kuduContext.createTable(kuduTableName, kuduTableSchema, kuduPrimaryKey, kuduTableOptions)

    /**
      * 1.Define a list of customers based on the case class
      * 2.转换为DF后进行一系列的sql操作
      */
    import ss.implicits._
    val customers = Array(
      BasicOperation("jane", 30, "new york"),
      BasicOperation("jordan", 18, "toronto"))
    val customersRDD = sc.parallelize(customers)
    val customersDF = customersRDD.toDF()
    /**
      * 1.首先定义kudu的属性
      * 2.将dataframe的data插入kudu表
      * 3.不推荐使用INSERT插入，因为spark任务可能会重复执行。推荐使用INSERT-IGNORE
      */
    val kuduOptions: Map[String, String] = Map(
      "kudu.table" -> kuduTableName,
      "kudu.master" -> kuduMasters
    )
    kuduContext.insertIgnoreRows(customersDF, kuduTableName)
    ss.read.options(kuduOptions).kudu.show()

    customersDF.createOrReplaceTempView("BasicOperation")

    //删除deleteRows
    val deleteKeysDF = ss.sql("select name from BasicOperation where age > 20")
    kuduContext.deleteRows(deleteKeysDF, kuduTableName)
    ss.read.options(kuduOptions).kudu.show()
    kuduContext.deleteTable("people")

    //UPSERT和UPDATE的区别在于UPSERT会在kudu表的行不存在的情况下会插入

    //更新插入upsertRows
    val newAndChangedCustomers = Array(
      BasicOperation("michael", 25, "chicago"),
      BasicOperation("denise", 43, "winnipeg"),
      BasicOperation("jordan", 19, "toronto"))
    val newAndChangedRDD = sc.parallelize(newAndChangedCustomers)
    val newAndChangedDF = newAndChangedRDD.toDF()
    kuduContext.upsertRows(newAndChangedDF, kuduTableName)
    ss.read.options(kuduOptions).kudu.show()

    //更新updateRows
    val modifiedCustomers = Array(BasicOperation("michael", 25, "toronto"))
    val modifiedCustomersRDD = sc.parallelize(modifiedCustomers)
    val modifiedCustomersDF = modifiedCustomersRDD.toDF()
    kuduContext.updateRows(modifiedCustomersDF, kuduTableName)
    ss.read.options(kuduOptions).kudu.show()

    //Reading with Native Kudu RDD,映射kudu表指定列的RDD[Row]
    val kuduTableProjColumns = Seq("name", "age")
    val custRDD = kuduContext.kuduRDD(sc, kuduTableName, kuduTableProjColumns)
    val custTuple = custRDD.map { case Row(name: String, age: Int) => (name, age) }
    custTuple.collect().foreach(println(_))

    //DATAFRAME write,只能使用append mode，相当于upsert
    val customersAppend = Array(
      BasicOperation("bob", 30, "boston"),
      BasicOperation("charlie", 23, "san francisco"))
    val customersAppendDF = sc.parallelize(customersAppend).toDF()
    customersAppendDF.write.options(kuduOptions).mode("append").kudu
    ss.read.options(kuduOptions).kudu.show()

    //使用源spark表建立kudu表,此过程相当于UPSERT
    val srcTableData = Array(
      BasicOperation("enzo", 43, "oakland"),
      BasicOperation("laura", 27, "vancouver"))
    val srcTableDF = sc.parallelize(srcTableData).toDF()
    srcTableDF.createOrReplaceTempView("source_table")
    ss.read.options(kuduOptions).kudu.createOrReplaceTempView(kuduTableName)
    ss.sql(s"insert into table $kuduTableName  select * from source_table")
    ss.read.options(kuduOptions).kudu.show()

    //Predicate pushdown
    ss.read.options(kuduOptions).kudu.createOrReplaceTempView(kuduTableName)
    val customerNameAgeDF = ss.sql(s"SELECT name, age FROM $kuduTableName WHERE age >= 30")
    customerNameAgeDF.show()
    customerNameAgeDF.explain()
  }
}