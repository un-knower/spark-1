package rabbitmq

import java.sql.DriverManager

import com.rabbitmq.client.ConnectionFactory
import org.json4s.jackson.JsonMethods.{compact, render}
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._

object Hive2MQ {
  def main(args: Array[String]): Unit = {

    val QUEUE_NAME="zhiqun_data"
    val EXCHANGE_NAME="BDExchange"
    val factory=new ConnectionFactory
    factory.setHost("192.168.0.40")
    factory.setUsername("lsrabbit")
    factory.setPassword("lsrabbit")
    val connection=factory.newConnection()
    val channel=connection.createChannel()

    channel.exchangeDeclare(EXCHANGE_NAME,"direct",true)
    channel.queueDeclare(QUEUE_NAME,true, false, false, null)
    channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, QUEUE_NAME)

    Class.forName("org.apache.hive.jdbc.HiveDriver")
    val conn=DriverManager.getConnection("jdbc:hive2://172.16.20.30:10000/zhiqun", "root", "253.com")
    val stmt = conn.createStatement()
    val rs=stmt.executeQuery("select * from mq ")
    while (rs.next()){
      val sp_code=rs.getString(1)
      val report=rs.getString(2)
      val content=rs.getString(3)
      val counts=rs.getString(4)
      val row=("sp_code"->sp_code)~("report"->report)~("content"->content)~("counts"->counts)
      val row2Json=compact(render(row))
      channel.basicPublish(EXCHANGE_NAME,QUEUE_NAME,null,row2Json.getBytes("UTF-8"))
    }
    channel.close()
    connection.close()

  }
}
