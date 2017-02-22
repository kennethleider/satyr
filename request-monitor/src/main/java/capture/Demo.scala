package capture

import kamon.Kamon

object Demo {
   def main(args: Array[String]): Unit = {
      Kamon.start()
      val start = new TagGroupGenerator(
         new TagGenerator("operator", "comcast" -> 7, "charter" -> 3, "brighthouse" -> 2),
         new TagGenerator("provider", "fearnet_hd" -> 1, "abc_hd" -> 9, "food_sd" -> 3)
      )

      val low = new TagGroupGenerator(
         new TagGenerator("operator", "comcast" -> 7, "charter" -> 3, "brighthouse" -> 2),
         new TagGenerator("provider", "fearnet_hd" -> 2, "abc_hd" -> 1, "food_sd" -> 6)
      )

      val providerOut = new TagGroupGenerator(
         new TagGenerator("operator", "comcast" -> 7, "charter" -> 3, "brighthouse" -> 2),
         new TagGenerator("provider", "fearnet_hd" -> 1, "abc_hd" -> 0, "food_sd" -> 3)
      )

      val operatorOut = new TagGroupGenerator(
         new TagGenerator("operator", "comcast" -> 7, "brighthouse" -> 2),
         new TagGenerator("provider", "fearnet_hd" -> 1, "abc_hd" -> 9, "food_sd" -> 3)
      )
      new Simulation(50, 15, 10, (2000, start), (600, low), (1200, providerOut), (2000, start), (1200, operatorOut)).run()
      Thread.sleep(10000)
      Kamon.shutdown()
   }
}
