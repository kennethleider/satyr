package capture

import net.opentsdb.core.{Aggregators, TSDB}
import net.opentsdb.utils.Config
import org.hbase.async.HBaseClient

import scala.collection.JavaConversions._

/**
  * Created by Ken on 1/31/2017.
  */
object Query {
   def main(args: Array[String]): Unit = {

      val db = new TSDB(new HBaseClient("cv-dvhmanager01.cv.scrum:2181"), new Config(false))
      db.getConfig.setAutoMetric(true)
      println(db.suggestMetrics(""))
      val query = db.newQuery()
      query.setTimeSeries("osa.comcast.ad-request", Map[String, String]("provider" -> "nbc.com"), Aggregators.NONE, false)
      query.setStartTime(1)
      val results = query.run()
      results.head.iterator().foreach { it =>
         println(it)
      }
      db.shutdown()
   }
}
