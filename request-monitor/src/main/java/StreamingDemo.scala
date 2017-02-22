
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.spark.{SparkConf, SparkContext, SparkEnv}
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._


object StreamingDemo {
   def main(args: Array[String]) {

      if (args.length < 4) {

         System.err.println("Usage: KafkaWordCount <zkQuorum> <group> <topics> <numThreads>")

         System.exit(1)

      }


      val Array(zkQuorum, group, topics, numThreads) = args

      val sparkConf = new SparkConf().setAppName("KafkaWordCount").setMaster("local[2]")

      val ssc = new StreamingContext(sparkConf, Seconds(2))

      //ssc.checkpoint("checkpoint")



      val topicMap = topics.split(",").map((_, numThreads.toInt)).toMap

      val lines = KafkaUtils.createStream(ssc, zkQuorum, group, topicMap).map{_._2}

      val words = lines.flatMap(_.split(" "))
//
      val wordCounts = words.map(x => (x, 1L)).reduceByKey(_ + _)

      wordCounts.foreachRDD { rdd =>
         println(Thread.currentThread().getName)
         println(SparkEnv.get.executorId)
         rdd.foreach { case (key, value) => println(s"$key: $value")}
      }


      ssc.start()

      ssc.awaitTermination()

   }
}
