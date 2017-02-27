import java.util.UUID

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

import scala.util.Random

class Simulation(producer : KafkaProducer[String, String], mean : Double, stddev : Double, min : Double) {

   def run(count : Int, generator: SessionPropertyGenerator) : Unit = {
      for (i <- 0 until count) {
         println("here")
         val wait = Math.max(min, new Random().nextGaussian() * stddev + mean).toLong
         val data = new ProducerRecord[String, String]("requests", UUID.randomUUID().toString, generator.next())
         producer.send(data)

         Thread.sleep(wait)
      }
   }

}
