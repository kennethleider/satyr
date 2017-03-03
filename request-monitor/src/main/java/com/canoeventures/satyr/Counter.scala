package com.canoeventures.satyr

import com.canoeventures.common.zookeeper.config.{ConfigManager, ConfigurationProxy, DeploymentManager}
import kamon.Kamon
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.ConsumerStrategies._
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies._
import org.apache.spark.streaming.{Duration, StreamingContext}

import scala.util.Try

abstract class Counter(val appName : String) {
   implicit def asCommitter[A](stream : InputDStream[A]) : CommitterStream[A] = new CommitterStream(stream)

   val config = ConfigManager.root.createManager("satyr")
   val sparkContext = {
      val sparkConf = new SparkConf().setAppName(appName)
      new SparkContext(sparkConf)
   }

   var ssc : StreamingContext = _

   def createStreamingContext() = {
      Try(ssc.stop(false, true))
      val duration = config.getDuration(appName + ".batch-duration").orElse(config.getDuration("batch-duration"))
      ssc = new StreamingContext(sparkContext, Duration(duration.get.toMillis))
   }

   def baseKafkaParams() : Map[String, Object] = Map[String, Object](
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean),
      "bootstrap.servers" -> config.getStringList("kafka.bootstrap-servers").get.mkString(",")
   )

   def createStream(topic : String, group : String = appName): InputDStream[ConsumerRecord[String, String]] = {
      val params = this.baseKafkaParams() + ("group.id" -> group)
      val strategy = Subscribe[String, String](Array(DeploymentManager.kafkaTopicProvider()(topic)), params)
      KafkaUtils.createDirectStream[String, String](ssc, PreferConsistent, strategy)

   }


   def start() = {
      Kamon.start(ConfigManager.root.createManager("kamon").config())
      config.registerAndCall(setup)
   }

   def setupStreams()

   def setup(config : ConfigurationProxy) = {
      Try(ssc.stop(false, true))
      createStreamingContext()
      setupStreams()
      ssc.start()
      ssc.awaitTermination()
   }
}
