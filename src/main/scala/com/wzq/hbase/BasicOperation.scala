package com.wzq.hbase


import org.apache.hadoop.hbase._
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.{SparkConf, SparkContext}

/**
  * hbase1.2.6 基本操作
  */
object BasicOperation {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("hbase").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val hconfig=HBaseConfiguration.create()
    hconfig.set(HConstants.ZOOKEEPER_QUORUM,"192.168.94.7:2181")
    val conn=ConnectionFactory.createConnection(hconfig)
    val tableName=TableName.valueOf("user")

    //admin：create, drop, list, enable and disable tables, add and drop table column families
    val admin=conn.getAdmin
    if (!admin.tableExists(tableName)) {
      println(tableName + " is not exist")
      admin.disableTable(tableName)
      admin.deleteTable(tableName)
    }
    val descriptor=new HTableDescriptor(tableName)
    descriptor.addFamily(new HColumnDescriptor("info1"))
    //admin.createTable(descriptor)
    //admin.listTables().foreach(println)//'user', {NAME => 'info1', BLOOMFILTER => 'ROW', VERSIONS => '1', IN_MEMORY => 'false', KEEP_DELETED_CELLS => 'FALSE', DATA_BLOCK_ENCODING => 'NONE', TTL => 'FOREVER', COMPRESSION => 'NONE', MIN_VERSIONS => '0', BLOCKCACHE => 'true', BLOCKSIZE => '65536', REPLICATION_SCOPE => '0'}
    //向hbase表添加数据
/*    val table=conn.getTable(tableName)
    try{
      val put=new Put(Bytes.toBytes("id"))
      put.addColumn(Bytes.toBytes("info1"),Bytes.toBytes("age"),Bytes.toBytes("23"))
      put.addColumn(Bytes.toBytes("info1"),Bytes.toBytes("name"),Bytes.toBytes("tom"))
      table.put(put)
    }finally {
      table.close()
      conn.close()
    }*/

    //获取数据
/*    val table=conn.getTable(tableName)
    val get=new Get(Bytes.toBytes("id"))
    val result=table.get(get)
    val values=result.getValue(Bytes.toBytes("info1"),Bytes.toBytes("age"))
    println(Bytes.toString(values))*/

    //删除数据
    val table=conn.getTable(tableName)
    val delete=new Delete(Bytes.toBytes("id"))
    delete.addColumn(Bytes.toBytes("info1"),Bytes.toBytes("age"))
    table.delete(delete)

    //scan表
    val s=new Scan()
    val scans=table.getScanner(s)
/*    val result=scans.next()
    val values=result.getValue(Bytes.toBytes("info1"),Bytes.toBytes("age"))
    println(Bytes.toString(values))*/

    var resultSet = Set[String]()
    val iterator=scans.iterator()
    while(iterator.hasNext){
      val result=iterator.next()
      val cells=result.rawCells()
      for(cell<-cells){
        println("行键:"+new String(CellUtil.cloneRow(cell)))
        println("列族:" + new String(CellUtil.cloneFamily(cell)))
        println("列名:" + new String(CellUtil.cloneQualifier(cell)))
        println("值:" + new String(CellUtil.cloneValue(cell)))
        println("时间戳:" + cell.getTimestamp())
        var str = Bytes.toString(CellUtil.cloneFamily(cell)) + ":" + Bytes.toString(CellUtil.cloneQualifier(cell))
        resultSet += str
        println("-------------------")
      }
    }
    println(resultSet)

    admin.close()
    conn.close()
    }
}
