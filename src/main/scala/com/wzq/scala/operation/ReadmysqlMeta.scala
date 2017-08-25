package com.wzq.scala.operation

import java.sql.DriverManager

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.clustering.KMeans
import org.apache.spark.ml.feature.{StringIndexer, VectorAssembler}

import scala.collection.mutable.ArrayBuffer

/**
  * 从数据库中读取列并转换为数组
  */
object ReadmysqlMeta {


  def main(args: Array[String]): Unit = {
    //数据库字段转换为数组
    Class.forName("com.mysql.jdbc.Driver").newInstance()
    val url="jdbc:mysql://192.168.94.7:3306/mystudy"
    val user="root"
    val password="zhiqun"
    val conn=DriverManager.getConnection(url,user,password)
    val stmt=conn.createStatement()
    conn.setAutoCommit(false)
    //Hive元数据表中字段
    val sql="select COLUMN_NAME from COLUMNS_V2 where CD_ID=96"
    val rs=stmt.executeQuery(sql)
    //不知道具体要存放多少元素，可以先构造ArrayBuffer,再调用toArray方法转化成Array
    val arrayBuff=ArrayBuffer[String]()
    while(rs.next()){
      arrayBuff+=rs.getString(1)
    }
    val arr=arrayBuff.toArray
    arr.mkString(",").foreach(print(_))
  }

}

