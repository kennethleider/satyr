import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

import scala.collection.JavaConverters._


object RequestGenerator extends App {

   val events = args(0).toInt
   val brokers = args(1)

   val props = Map[String, Object]("bootstrap.servers" -> brokers,
      "client.id" -> "ScalaProducerExample",
      "key.serializer" -> "org.apache.kafka.common.serialization.StringSerializer",
      "value.serializer" -> "org.apache.kafka.common.serialization.StringSerializer"
   )

   val producer = new KafkaProducer[String, String](props.asJava)
   //println(producer.partitionsFor("requests"))
   val t = System.currentTimeMillis()

   for (nEvents <- 0 to events) {
      val data = new ProducerRecord[String, String]("requests", nEvents.toString, "sample one")
      producer.send(data)
   }


   System.out.println("sent per second: " + events * 1000 / (System.currentTimeMillis() - t))
   producer.close()
}
