import kafka.admin.AdminUtils
import kafka.common.OffsetMetadata
import kafka.consumer.PartitionTopicInfo
import kafka.utils.ZkUtils
import org.apache.kafka.clients.consumer.{KafkaConsumer, OffsetAndMetadata}
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer

import scala.util.Try
import scala.collection.JavaConverters._

object ManageTopic extends App {
   val zkQuorum = args(0)
   val zookeeperClient = ZkUtils(zkQuorum, 10000, 10000, false)

   //AdminUtils.deleteTopic(zookeeperClient, "requests")
//   AdminUtils.deleteAllConsumerGroupInfoForTopicInZK(zookeeperClient, "requests")
   //AdminUtils.deleteConsumerGroupInZK(zookeeperClient, "satyr")
  //AdminUtils.createTopic(zookeeperClient, "requests", 20, 2)
   println(AdminUtils.fetchTopicMetadataFromZk("requests", zookeeperClient).partitionMetadata().asScala.map { pm => pm.leader()})
   AdminUtils.

//   val props = Map[String, Object](
//      "key.deserializer" -> classOf[StringDeserializer],
//      "value.deserializer" -> classOf[StringDeserializer],
//      "group.id" -> "satyr",
//      "auto.offset.reset" -> "latest",
//      "enable.auto.commit" -> (false: java.lang.Boolean),
//      "bootstrap.servers" -> "cv-dvhdata04.cv.scrum:9092"
//   )
//
//   val consumer = new KafkaConsumer[String, String](props.asJava)
//   val updates = consumer.partitionsFor("requests").asScala.map { pi =>
//      new TopicPartition(pi.topic(), pi.partition()) -> new OffsetAndMetadata(0, null)
//   }.toMap
//   consumer.commitSync(updates.asJava)


}
