package com.wzq.spark.kafka

import java.util.{Collections, HashMap}

import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer}

/**
  * Created by wangzhiqun on 2017/8/18.
  */
object Consumer {
  def main(args: Array[String]): Unit = {
    val brokers = "192.168.94.7:9092"
    val topic = "kafkaStream"
    val props = new HashMap[String, Object]()
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers)
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "kafkaStream")
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    val consumer = new KafkaConsumer[String, String](props)
    consumer.subscribe(Collections.singleton(topic))
    val records = consumer.poll(1000)
    val iterator=records.iterator()
    while(iterator.hasNext){
      val i=iterator.next()
      println(i)
    }
    consumer.close()
  }
}
