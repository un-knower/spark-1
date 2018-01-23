package rabbitmq

import com.rabbitmq.client.AMQP.BasicProperties
import com.rabbitmq.client.{ConnectionFactory, MessageProperties, QueueingConsumer}

object WindowsMq {
  def main(args: Array[String]): Unit = {
    val QUEUE_NAME="zhiqun_test"
    val EXCHANGE_NAME="zqExchange"
    val factory=new ConnectionFactory
    factory.setHost("localhost")
    factory.setUsername("zhiqun")
    factory.setPassword("zhiqun")
    val connection=factory.newConnection()
    val channel=connection.createChannel()
    //声明队列，主要为了防止消息接收者先运行此程序，队列还不存在时创建队列。
    channel.queueDeclare(QUEUE_NAME,true, false, false, null)

    //创建临时队列,非持久的、唯一的且自动删除的队列
   // val  queueName = channel.queueDeclare().getQueue()
    // //把队列绑定到路由上  ，设置binding
    //channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "")



    //publish
/*    val message="hello mq"
    channel.basicPublish("",QUEUE_NAME,null,message.getBytes("UTF-8"))
    println(" [x] Sent '" + message + "'")*/

    //consume
/*    val consumer=new QueueingConsumer(channel)
    channel.basicConsume(QUEUE_NAME,true,consumer)
    while(true){
      val delivery=consumer.nextDelivery()
      val message=new String(delivery.getBody)
      println(" [x] Received '" + message + "'")
    }*/

/*    val builder=new BasicProperties().builder()
    //设置消息持久化，1： 非持久化 2：持久化  Persistent
    builder.deliveryMode(2)
    val properties=builder.build()*/

    for(i<- 1 to 10){
      val dots="."*i
      val message="hello mq"+dots+dots.length
      //消息持久化
      channel.basicPublish("",QUEUE_NAME,MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes())
      println(" [x] Sent '" + message + "'")
    }



    channel.close()
    connection.close()
  }

}
