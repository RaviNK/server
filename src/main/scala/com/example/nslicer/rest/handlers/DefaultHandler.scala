package com.example.nslicer.rest.handlers

import io.undertow.server.{HttpServerExchange, HttpHandler}

/**
  * Created by vishnu on 21/5/16.
  */
class DefaultHandler extends HttpHandler {
  override def handleRequest(exchange: HttpServerExchange): Unit = {
    exchange.getResponseSender.send("{\"status\":\"ok\"}")
  }
}
