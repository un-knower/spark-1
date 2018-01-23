package rabbitmq

import com.rabbitmq.client.{ConnectionFactory, QueueingConsumer}

object WindowsMq2 {
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

    val hashCode=this.hashCode()
    println(hashCode + " [*] Waiting for messages. To exit press CTRL+C")


    //公平转发，只有在消费者空闲的时候会发送下一条信息。设置最大服务转发消息数量
    channel.basicQos(1)

    val consumer=new QueueingConsumer(channel)

    /**
      * 若消费者进程中断，希望不丢失任何信息。将消息传递给另一个消费者。
      * 保证消息永远不会丢失，RabbitMQ支持消息应答message acknowledgments
      * 消费者发送应答给RabbitMQ，告诉它信息已经被接收和处理，然后RabbitMQ可以自由的进行信息删除
      * 如果消费者被杀死而没有发送应答，RabbitMQ会认为该信息没有被完全的处理，然后将会重新转发给别的消费者
      */

    //打开应答机制
    val ack=false
    //channel.basicConsume(QUEUE_NAME,true,consumer)
    channel.basicConsume(QUEUE_NAME,ack,consumer)


    while(true){
      val delivery=consumer.nextDelivery()
      val message=new String(delivery.getBody)
      println(hashCode + " [x] Received '" + message + "'")
      // 每个点耗时1s
      message.foreach(i=>{
        if(i=='.') Thread.sleep(1000)
      })
      println(hashCode + " [x] Done")

      //在每次处理完成一个消息后，手动显示发送一次应答
      channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false)

    }

  }
}
