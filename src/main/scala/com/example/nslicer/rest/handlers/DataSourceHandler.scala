package com.example.nslicer.rest.handlers

import com.example.nslicer.models.DataSource
import org.slf4j.LoggerFactory
import spray.json._
import com.example.nslicer.utils.UNDERTOW_HELPERS
import io.undertow.server.{HttpServerExchange, HttpHandler}
import org.apache.commons.io.IOUtils

/**
  * Created by vishnu on 21/5/16.
  */
class DataSourceHandler extends HttpHandler {
  override def handleRequest(exchange: HttpServerExchange): Unit = {

    try {
      if (exchange.isInIoThread) {
        exchange.dispatch(this)
      } else {
        exchange.startBlocking
        exchange.getResponseHeaders.add(UNDERTOW_HELPERS.ACCESS_CONTROL_ALLOW_ORIGIN._1, UNDERTOW_HELPERS.ACCESS_CONTROL_ALLOW_ORIGIN._2)
          .add(UNDERTOW_HELPERS.ACCESS_CONTROL_ALLOW_HEADERS._1, UNDERTOW_HELPERS.ACCESS_CONTROL_ALLOW_HEADERS._2)
          .add(UNDERTOW_HELPERS.ACCESS_CONTROL_ALLOW_CREDENTIALS._1, UNDERTOW_HELPERS.ACCESS_CONTROL_ALLOW_CREDENTIALS._2)
          .add(UNDERTOW_HELPERS.ACCESS_CONTROL_ALLOW_METHODS._1, UNDERTOW_HELPERS.ACCESS_CONTROL_ALLOW_METHODS._2)
          .add(UNDERTOW_HELPERS.ACCESS_CONTROL_MAX_AGE._1, UNDERTOW_HELPERS.ACCESS_CONTROL_MAX_AGE._2)




        val requestJson = new String(IOUtils.toByteArray(exchange.getInputStream)).parseJson.asJsObject

        val response = if (requestJson.getFields("action").nonEmpty) {
          requestJson.getFields("action").head.asInstanceOf[JsString].value match {
            case "new" => DataSourceHandler.createDataSource(requestJson)
            case "prepare" => DataSourceHandler.prepareDataSource(requestJson)
            case "get" => DataSourceHandler.getDataSource(requestJson)
            case _ => (0, null)
          }

        } else (0, null)

        if (response._1 != 0) {
          exchange.setStatusCode(response._1)
          exchange.getResponseSender.send(response._2.prettyPrint)
        } else {
          exchange.setStatusCode(500)
          exchange.getResponseSender.send(JsObject(Map("status" -> JsString("failed"), "message" -> JsString("Invalid Request Format"))).prettyPrint)
        }

      }

    }
    catch {
      case e: Exception => {
        exchange.setStatusCode(500)
        exchange.getResponseSender.send(JsObject(Map("status" -> JsString("failed"), "message" -> JsString(e.getLocalizedMessage))).prettyPrint)
        DataSourceHandler.LOG.error("Exception while Handling Request", e)
      }
    }

  }
}

object DataSourceHandler {
  val LOG = LoggerFactory.getLogger(this.getClass)


  def createDataSource(requestJson: JsObject): (Int, JsObject) = {

    if (requestJson.getFields("data").nonEmpty) {
      var dataSource = DataSource.fromJson(requestJson.getFields("data").head.asJsObject)

      dataSource = DataSource.getDataSource(DataSource.saveToDb(dataSource))

      if (dataSource != null) {
        (200, JsObject("status" -> JsString("ok"), "data" -> dataSource.toJson))
      } else {
        (500, JsObject(Map("status" -> JsString("failed"), "message" -> JsString("Unable to save Please Try again"))))
      }
    } else (0, JsObject())
  }

  def prepareDataSource(requestJson: JsObject): (Int, JsObject) = {
    val sourceId = if (requestJson.getFields("sourceId").nonEmpty) {
      requestJson.getFields("sourceId").head.asInstanceOf[JsNumber].value.toLong
    } else 0

    val source = DataSource.getDataSource(sourceId)
    if (source != null)
      (200, DataSource.prepareSource(source))
    else
      (0, null)
  }

  def getDataSource(requestJson: JsObject): (Int, JsObject) = {

    val source = DataSource.getDataSource(
      if (requestJson.getFields("sourceId").nonEmpty)
        requestJson.getFields("sourceId").head.asInstanceOf[JsNumber].value.toLong
      else 0
    )
    if (source != null) {
      (200, JsObject("status" -> JsString("ok"), "data" -> source.toJson))
    } else (0, null)
  }

}
