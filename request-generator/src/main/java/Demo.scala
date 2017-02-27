import org.apache.kafka.clients.producer.KafkaProducer

import scala.collection.JavaConverters._

object Demo {
   def main(args: Array[String]): Unit = {

      val props = Map[String, Object](
         "bootstrap.servers" -> "cv-dvhdata04.cv.scrum:9092",
         "group.id" -> "satyr",
         "key.serializer" -> "org.apache.kafka.common.serialization.StringSerializer",
         "value.serializer" -> "org.apache.kafka.common.serialization.StringSerializer"
      )

      val producer = new KafkaProducer[String, String](props.asJava)

      val start = new SessionPropertyGenerator(
         operatorSelector = new Selector("comcast" -> 7, "charter" -> 3, "cox" -> 2),
         providerIdSelector = new Selector("fearnet_hd" -> 1, "abc_hd" -> 9, "food_sd" -> 3),
         platformSelector = new Selector("stb" -> 9, "managedIP" -> 1),
         backOfficeSelector = Map(
            "comcast" -> new Selector("PLEX" -> 7, "Xi3" -> 3),
            "charter" -> new Selector("TWC" -> 7, "BHN" -> 3, "CHTR" -> 3),
            "cox" -> new Selector("Legacy" -> 7, "Watermark" -> 3)
         ),
         regionSelectors = Map(
            "comcast" -> new Selector("A" -> 7, "X" -> 3),
            "charter" -> new Selector("C" -> 7, "B" -> 3, "H" -> 3),
            "cox" -> new Selector("E" -> 7, "F" -> 3)
         )

      )

//      val low = new TagGroupGenerator(
//         new TagGenerator("operator", "comcast" -> 7, "charter" -> 3, "brighthouse" -> 2),
//         new TagGenerator("provider", "fearnet_hd" -> 2, "abc_hd" -> 1, "food_sd" -> 6)
//      )
//
//      val providerOut = new TagGroupGenerator(
//         new TagGenerator("operator", "comcast" -> 7, "charter" -> 3, "brighthouse" -> 2),
//         new TagGenerator("provider", "fearnet_hd" -> 1, "abc_hd" -> 0, "food_sd" -> 3)
//      )
//
//      val operatorOut = new TagGroupGenerator(
//         new TagGenerator("operator", "comcast" -> 7, "brighthouse" -> 2),
//         new TagGenerator("provider", "fearnet_hd" -> 1, "abc_hd" -> 9, "food_sd" -> 3)
//      )

      val simulation = new Simulation(producer, 50, 15, 10)
      simulation.run(10, start)

      producer.close()
   }
}
