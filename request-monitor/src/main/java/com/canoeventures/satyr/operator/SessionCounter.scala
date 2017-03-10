package com.canoeventures.satyr.operator

import com.canoeventures.common_cao.domain.Session
import com.canoeventures.common_cao.util.SerializationUtils
import com.canoeventures.satyr.{CommitterStream, Counter}
import kamon.Kamon

case class Key(operator : String, providerId : String, platform : String, region : String, backOffice : String) {
   def tags = Map[String, String](
      "operator" -> operator,
      "providerId" -> providerId,
      "platform" -> platform,
      "region" -> region,
      "backOffice" -> backOffice
   )
}

class SessionCounter extends Counter("SessionCounter") with Serializable {

   override def setupStreams() : Unit = {
      val stream = new CommitterStream(createStream("requests"))
      stream.withCommit {
         stream.ifNotEmpty { rdd =>
            logger.warn(s"Starting new rdd with size: ${rdd.count()}")
            val keys = rdd.map { record =>
               new SerializationUtils[Session](Session.SCHEMA$).deserialize(record.value())
            }.map { session =>
               Key(session.getOperator.toString, session.getProviderId.toString, session.getPlatform.toString, session.getRegion.toString, session.getVodBackOffice.toString)
            }

            val keyCounts = keys.map(x => (x, 1L)).reduceByKey(_ + _)

            keyCounts.foreach { case (key, value) =>
               Kamon.metrics.counter("osa.sessions", key.tags).increment(value)
            }
         }
      }
   }
}

object SessionCounter {
   def main(args: Array[String]) {
      new SessionCounter().start()
   }
}