import java.util.Properties

import kafka.admin
import kafka.admin.ReassignPartitionsCommand.ReassignPartitionsCommandOptions
import kafka.admin.{AdminUtils, RackAwareMode, ReassignPartitionsCommand}
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
   //AdminUtils.deleteAllConsumerGroupInfoForTopicInZK(zookeeperClient, "requests")
  // AdminUtils.deleteConsumerGroupInZK(zookeeperClient, "satyr")
//   createTopic(zookeeperClient, "request", 20, 2)
//

   AdminUtils.fetchTopicMetadataFromZk("request", zookeeperClient).partitionMetadata().asScala.foreach { pm => println(pm.leader()) }

   val props = Map[String, Object](
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "satyr",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean),
      "bootstrap.servers" -> "cv-dvhdata04.cv.scrum:9092"
   )

   val consumer = new KafkaConsumer[String, String](props.asJava)
   val topicPartitions = consumer.partitionsFor("request").asScala.map { pi => new TopicPartition(pi.topic(), pi.partition())}
   consumer.assign(topicPartitions.asJava)
   topicPartitions.foreach { tp => println("b: " + consumer.position(tp))}
   consumer.seekToBeginning(topicPartitions.asJava)
   topicPartitions.foreach { tp => println("a: " + consumer.position(tp))}
   consumer.commitSync()


   def createTopic(zkUtils: ZkUtils,
                   topic: String,
                   partitions: Int,
                   replicationFactor: Int,
                   topicConfig: Properties = new Properties,
                   rackAwareMode: RackAwareMode = RackAwareMode.Enforced) {
      val brokerMetadatas = AdminUtils.getBrokerMetadatas(zkUtils, rackAwareMode)
      val replicaAssignment = AdminUtils.assignReplicasToBrokers(brokerMetadatas, partitions, replicationFactor)
      AdminUtils.createOrUpdateTopicPartitionAssignmentPathInZK(zkUtils, topic, replicaAssignment, topicConfig, true)
   }


}
