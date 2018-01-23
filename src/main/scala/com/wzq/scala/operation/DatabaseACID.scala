package com.wzq.scala.operation

import java.sql.DriverManager

object DatabaseACID {
  def main(args: Array[String]): Unit = {

    Class.forName("oracle.jdbc.driver.OracleDriver")
    val url="jdbc:oracle:thin:@192.168.0.194:1521:smsdb"
    val user="smsdb"
    val password="chuanglan789"
    val conn=DriverManager.getConnection(url,user,password)

    //测试链接
    println("连接成功")

    //插入数据库，以append模式
    val ps=conn.prepareStatement("INSERT INTO JIAYUAN (ID,EDUCATION,SALARY,NATION) VALUES(?,?,?,?)")
    ps.setString(1,"q")
    ps.setString(2,"w")
    ps.setString(3,"e")
    ps.setString(4,"r")
    ps.executeUpdate()




  }
}
