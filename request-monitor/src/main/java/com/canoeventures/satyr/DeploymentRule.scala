package com.canoeventures.satyr

import com.canoeventures.common.zookeeper.config.ConfigManager
import com.typesafe.config.Config
import kamon.opentsdb.StaticValueRule

class DeploymentRule(config : Config) extends StaticValueRule(ConfigManager.root.getString("deployment", "").toLowerCase)
