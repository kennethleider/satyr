package capture

import scala.util.Random

/**
  * Created by Ken on 2/7/2017.
  */
class TagGenerator(key : String, weights : (String, Int)*) extends Iterator[(String, String)] {
   val total = weights.map { _._2 }.sum
   val values = weights.map { weight => weight._2.toDouble / total -> weight._1 }
   val random = new Random()

   override def hasNext: Boolean = true

   override def next(): (String, String) = {
      var roll = random.nextDouble()

      values.find { e => roll -= e._1; roll < 0 }.map { e => (key, e._2) }.get
   }
}
