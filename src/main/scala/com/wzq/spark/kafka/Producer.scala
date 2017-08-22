package com.wzq.spark.kafka

import java.util.HashMap

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}

object Producer {
  def main(args: Array[String]): Unit = {
    val brokers="192.168.94.7:9092"
    val topic ="kafkaStream"
    val props = new HashMap[String, Object]()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers)
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
      "org.apache.kafka.common.serialization.StringSerializer")
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
      "org.apache.kafka.common.serialization.StringSerializer")
    val producer = new KafkaProducer[String, String](props)
    //send messages
    while(true){
      for (i <- 0 to 10) {
        val ret=producer.send(new ProducerRecord(topic, "messages:" + i))
        val metadata=ret.get()  // 打印出 metadata
        println("i="+i+",offset="+metadata.offset()+",partition="+metadata.partition())
      }
      Thread.sleep(3000)
    }
    producer.close()
  }
}
