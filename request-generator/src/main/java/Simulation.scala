import java.util.UUID

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

import scala.util.Random

class Simulation(producer : KafkaProducer[String, Array[Byte]], mean : Double, stddev : Double, min : Double) {

   def run(count : Int, generator: SessionPropertyGenerator) : Unit = {
      (0 until count).foreach { i =>
         val wait = Math.max(min, new Random().nextGaussian() * stddev + mean).toLong
         val data = new ProducerRecord[String, Array[Byte]]("KEN2.requests", UUID.randomUUID().toString, generator.next())
         println(new String(data.value()))

         producer.send(data)

         Thread.sleep(wait)
      }
   }

}
