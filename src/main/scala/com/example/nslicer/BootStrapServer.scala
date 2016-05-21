package com.example.nslicer

import com.example.nslicer.connectors.MysqlClient
import com.example.nslicer.rest.{Routes, UndertowApiServer}
import com.typesafe.config.{ConfigFactory, Config}
import org.slf4j.LoggerFactory

/**
  * Created by vishnu on 21/5/16.
  */
object BootStrapServer {

  val LOG = LoggerFactory.getLogger(this.getClass)

  val config: Config = ConfigFactory.load()

  val mysqlClient = new MysqlClient(config)

  val apiServer = new UndertowApiServer(config, new Routes)

  def main(args: Array[String]) {
    println("Starting Server")
    LOG.info("Starting Server:" + config.getString("version") + " Logging ")

    apiServer.startServer

    println("Main Function Control Exited")
  }
}
