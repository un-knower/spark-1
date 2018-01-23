package rabbitmq

import com.rabbitmq.client.ConnectionFactory

object MQUtils extends Serializable{
  val QUEUE_NAME="zhiqun_test"
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
  channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, QUEUE_NAME)
}
