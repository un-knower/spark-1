package rabbitmq

import com.rabbitmq.client.AMQP.BasicProperties
import com.rabbitmq.client.ConnectionFactory

object MQSendDemo {
  def main(args: Array[String]): Unit = {

    /**
      * 1.设置队列名称
      * 2.创建连接到rabbitmq
      * 3.设置rabbitmq所在主机，用户名，密码
      * 4.创建一个连接
      * 5.创建一个频道
      * 6.指定一个队列
      * 7.往队列发送消息
      * 8.关闭频道和连接
      */

    val QUEUE_NAME="zhiqun_test"
    val EXCHANGE_NAME="zqExchange"
    val factory=new ConnectionFactory
    factory.setHost("192.168.0.40")
    factory.setUsername("lsrabbit")
    factory.setPassword("lsrabbit")
    factory.setPort(5672)
    val connection=factory.newConnection()
    val channel=connection.createChannel()


/*    channel.queueDeclare(QUEUE_NAME, false, false, false, null)
    val message=(1,2)
    channel.basicPublish("",QUEUE_NAME,null,message.toString().getBytes("UTF-8"))
    println("sent:"+message+"....")*/

    channel.exchangeDeclare(EXCHANGE_NAME,"direct",true)
    channel.queueDeclare(QUEUE_NAME,true, false, false, null)
    channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, QUEUE_NAME)

    //设置发送消息的属性
/*    val builder=new BasicProperties().builder()
    builder.contentType("json")
    builder.contentEncoding("UTF-8")
    val properties=builder.build()*/


    for(i<- 1 to 1000000){
      val message=i+"aaaaaaaaa"
      channel.basicPublish(EXCHANGE_NAME,QUEUE_NAME,null,message.getBytes("UTF-8"))
      //channel.basicPublish(EXCHANGE_NAME,QUEUE_NAME,properties,message.getBytes())

      println(message)
    }

    channel.close()
    connection.close()


  }
}
