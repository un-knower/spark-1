package rabbitmq

import com.rabbitmq.client._

object MQConsumeDemo {
  def main(args: Array[String]): Unit = {
    val QUEUE_NAME="zhiqun_test"
    val EXCHANGE_NAME="zhiqunExchange"
    val factory=new ConnectionFactory
    factory.setHost("192.168.0.40")
    factory.setUsername("lsrabbit")
    factory.setPassword("lsrabbit")
    val connection=factory.newConnection()
    val channel=connection.createChannel()
    channel.exchangeDeclare(EXCHANGE_NAME,"direct",true)
    //声明队列，主要为了防止消息接收者先运行此程序，队列还不存在时创建队列。
    channel.queueDeclare(QUEUE_NAME,true, false, false, null)
    //加上queuebind才不会出错
    channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, QUEUE_NAME)

/*    val consumer=new DefaultConsumer(channel){
      override def handleDelivery(consumerTag: String, envelope: Envelope, properties: AMQP.BasicProperties, body: Array[Byte]): Unit = {
        var message=new String(body,"UTF-8")
        println(message)
      }
    }
    channel.basicConsume(QUEUE_NAME, true, consumer)*/

    val consumer=new QueueingConsumer(channel)
    channel.basicConsume(QUEUE_NAME,true,consumer)
    while(true){
      val delivery=consumer.nextDelivery()
      val message=new String(delivery.getBody)
      println(message)
    }
    channel.close()
    connection.close()

  }
}
