import scala.util.Random


class Selector(weights : (String, Int)*) extends Iterator[String] {
   val total = weights.map { _._2 }.sum
   val values = weights.map { weight => weight._2.toDouble / total -> weight._1 }
   val random = new Random()

   override def hasNext: Boolean = true

   override def next(): String = {
      var roll = random.nextDouble()

      values.find { e => roll -= e._1; roll < 0 }.map { _._2 }.get
   }
}
