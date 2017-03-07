package com.canoeventures.satyr.operator

import com.canoeventures.satyr.Counter
import kamon.Kamon

case class Key(operator : String, providerId : String, platform : String, region : String, backOffice : String) {
   def tags = Map[String, String]("operator" -> operator)
}

class SessionCounter extends Counter("SessionCounter") {

   override def setupStreams() : Unit = {
      val stream = createStream("requests")
      stream.withCommit { rdd =>
         logger.warn(s"Starting new rdd with size: ${rdd.count()}")
         val keys = rdd.map { record =>
            val Array(operator, providerId, platform, region, backOffice) = record.value.split("-")
            Key(operator, providerId, platform, region, backOffice)
         }
         val keyCounts = keys.map(x => (x, 1L)).reduceByKey(_ + _)

         keyCounts.foreach { case (key, value) =>
            Kamon.metrics.counter("osa.sessions", key.tags).increment(value)
         }
      }
   }
}

object SessionCounter {
   def main(args: Array[String]) {
      new SessionCounter().start()
   }
}