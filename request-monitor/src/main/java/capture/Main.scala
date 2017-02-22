package capture

import net.opentsdb.core.TSDB
import net.opentsdb.utils.Config
import org.hbase.async.HBaseClient

import scala.collection.JavaConverters._

/**
  * Created by Ken on 1/31/2017.
  */
object Main {
   def main(args: Array[String]): Unit = {

      val db = new TSDB(new HBaseClient("cv-dvhmanager01.cv.scrum:2181"), new Config(false))
      db.getConfig.setAutoMetric(true)
      db.suggestMetrics("").iterator().asScala.foreach { metric =>
         println(metric)
         db.deleteUidAsync("metric", metric)
      }
//      db.suggestTagNames("").iterator().asScala.foreach { tagName =>
//         db.deleteUidAsync("tagk", tagName)
//      }
//      db.suggestTagValues("").iterator().asScala.foreach { tagValue =>
//         db.deleteUidAsync("tagv", tagValue)
//      }


      //val time = Instant.now().getEpochSecond
      val time = 1486417559
      //db.deleteUidAsync("metric", "osa.comcast.ad-request")
      //db.addPoint("counter/osa.comcast.ad-request", time, 2, Map("provider" -> "abc.com")).join()
//      db.addPoint("osa.comcast.ad-request", Instant.now().getEpochSecond, 2, Map("provider" -> "nbc.com")).join()
//      db.addPoint("osa.comcast.ad-request", Instant.now().getEpochSecond, 3, Map("provider" -> "cbs.com")).join()
      db.dropCaches()
      db.flush()
      db.shutdown()
   }
}
