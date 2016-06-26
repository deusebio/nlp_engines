package com.intesasanpaolo.bip.logging

import org.apache.log4j.Logger
import org.apache.log4j.Level._

trait Logging {

  Logger.getRootLogger().setLevel(OFF)
  Logger.getLogger("org").setLevel(ERROR)
  // Logger.getLogger("com").setLevel(ALL)

  protected def logger = Logger.getLogger("com.intesasanpaolo.bip")
}

