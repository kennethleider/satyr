import kafka.admin.AdminUtils
import kafka.utils.ZkUtils

import scala.util.Try

object ManageTopic extends App {
   val zkQuorum = args(0)
   val zookeeperClient = ZkUtils(zkQuorum, 10000, 10000, false)

   Try(AdminUtils.deleteTopic(zookeeperClient, "requests"))
   AdminUtils.createTopic(zookeeperClient, "requests", 20, 2)
}
