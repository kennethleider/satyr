
import org.apache.kafka.clients.consumer.{KafkaConsumer, OffsetAndMetadata}
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.{KafkaUtils, _}
import scala.collection.JavaConverters._

case class Key(operator : String, providerId : String, platform : String, region : String, backOffice : String)

object StreamingDemo {
   def main(args: Array[String]) {

      val sparkConf = new SparkConf().setAppName("KafkaWordCount").setMaster("local[*]")

      val ssc = new StreamingContext(sparkConf, Seconds(2))

      val kafkaParams = Map[String, Object](
         "key.deserializer" -> classOf[StringDeserializer],
         "value.deserializer" -> classOf[StringDeserializer],
         "group.id" -> "satyr",
         "auto.offset.reset" -> "latest",
         "enable.auto.commit" -> (false: java.lang.Boolean),
         "bootstrap.servers" -> "cv-dvhdata04.cv.scrum:9092"
      )

//      val consumer = new KafkaConsumer[String, String](kafkaParams.asJava)
//      val updates = consumer.partitionsFor("requests").asScala.map { pi =>
//         new TopicPartition(pi.topic(), pi.partition()) -> new OffsetAndMetadata(0, null)
//      }.toMap
//      consumer.commitSync(updates.asJava)

      val stream = KafkaUtils.createDirectStream[String, String](
         ssc,
         PreferConsistent,
         Subscribe[String, String](Array("requests"), kafkaParams)
      )


      stream.foreachRDD { rdd =>
         val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
         val keys = rdd.map { record =>
            val Array(operator, providerId, platform, region, backOffice) = record.value.split("-")
            Key(operator, providerId, platform, region, backOffice)
         }
         val keyCounts = keys.map(x => (x, 1L)).reduceByKey(_ + _)
         keyCounts.foreach { case (key, value) => println(s"$key: $value")}
         stream.asInstanceOf[CanCommitOffsets].commitAsync(offsetRanges)
      }

      ssc.start()

      ssc.awaitTermination()
   }
}
