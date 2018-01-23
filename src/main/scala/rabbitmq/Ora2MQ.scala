package rabbitmq

import java.sql.DriverManager
import java.util.Properties

import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods.{compact, _}
import com.rabbitmq.client.ConnectionFactory
import org.apache.spark.sql.SparkSession

object Ora2MQ {
  def main(args: Array[String]): Unit = {
    val ss=SparkSession.builder().appName("mq").master("local[3]")
      .getOrCreate()
    import ss.implicits._

    Class.forName("oracle.jdbc.driver.OracleDriver")
    val url="jdbc:oracle:thin:@192.168.0.194:1521:smsdb"
    val driver="oracle.jdbc.driver.OracleDriver"
    val properties=new Properties()
    properties.put("user","smsdb")
    properties.put("password","chuanglan789")
    properties.put("driver",driver)


/*    val QUEUE_NAME="zhiqun_test"
    val EXCHANGE_NAME="zqExchange"
    val factory=new ConnectionFactory
    factory.setHost("192.168.0.40")
    factory.setUsername("lsrabbit")
    factory.setPassword("lsrabbit")
    factory.setPort(5672)
    val connection=factory.newConnection()
    val channel=connection.createChannel()
    channel.exchangeDeclare(EXCHANGE_NAME,"direct",true)
    channel.queueDeclare(QUEUE_NAME,true, false, false, null)
    channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, QUEUE_NAME)*/

/*    val conn=DriverManager.getConnection(url,user,password)
    val statement = conn.createStatement
    val rs=statement.executeQuery("select * from AA_3")
    /**
      * 使用json4s将数据库的数据以json格式输入rabbitmq
      */
    while (rs.next()){
      val name=rs.getString(1)
      val content=rs.getString(2)
      val row=(name->content)
      val row2Json=compact(render(row))
      channel.basicPublish("",QUEUE_NAME,null,row2Json.getBytes("UTF-8"))
    }
    channel.close()
    connection.close()*/

    /**
      * map等算子内部使用了外部定义的变量和函数，从而引发Task未序列化问题。
      * 将成员变量或成员函数写入object中
      */
/*    ss.read.jdbc(url,"AA_3",properties).map{
      case (r)=>
        val row=(r(0).toString->r(1).toString)
        compact(render(row))
    }
      .foreach{i=>
        MQUtils.channel.basicPublish("",MQUtils.QUEUE_NAME,null,i.getBytes("UTF-8"))
      }
    MQUtils.channel.close()
    MQUtils.connection.close()*/

/*    ss.read.jdbc(url,"AA_3",properties).map{
      case (r)=>
        val row=(r(0).toString->r(1).toString)
        compact(render(row))
    }
      .foreach{i=>
        channel.basicPublish("",QUEUE_NAME,null,i.getBytes("UTF-8"))
      }*/

  }
}
