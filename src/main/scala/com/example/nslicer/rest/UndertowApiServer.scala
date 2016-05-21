package com.example.nslicer.rest

import com.typesafe.config.Config
import io.undertow.Undertow
import org.slf4j.LoggerFactory

/**
  * Created by vishnu on 21/5/16.
  */
class UndertowApiServer(config: Config, routes: Routes) {
  var isServerActive: Boolean = false

  private val LOG = LoggerFactory.getLogger(classOf[UndertowApiServer])

  private var server: Undertow = null
  private val port = config.getInt("server.port")
  private val interfaceName = config.getString("server.interface")

  def startServer: Boolean = {
    if (isServerActive) {
      LOG.error("The Server is Already Active on http://" + interfaceName + ":" + port)
      false
    } else {
      try {
        server = Undertow.builder.addHttpListener(port, interfaceName)
          .setHandler(routes.getAllHandlers)
          .build

        server.start()
        isServerActive = true
        LOG.info("The Server is Active on http://" + interfaceName + ":" + port)
        true
      } catch {
        case e: Exception => LOG.error("Server Initialization Failed ", e)
          false
      }
    }
  }


  def stopServer: Boolean = {
    try {
      server.stop()
      server = null
    }
    catch {
      case e: NullPointerException => {
        LOG.debug("Server Has Not been Initialized Shutdown Sequence Complete")
      }
    }
    isServerActive = false
    true
  }


}
