package capture

import kamon.Kamon

import scala.util.Random

class Simulation(mean : Double, stddev : Double, min : Double, stages : (Int, TagGroupGenerator)*) {


   def run(): Unit = {
      stages.foreach { case (count, generator) =>
         for (i <- 0 to count) {
            val wait = Math.max(min, new Random().nextGaussian() * stddev + mean).toLong
            val tags = generator.next().toMap
            Kamon.metrics.counter("osa.request", tags).increment()
            Kamon.metrics.counter("osa.opportunity", tags).increment(wait / 8L)
            Thread.sleep(wait)
         }
      }

   }

}
