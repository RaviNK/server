package com.example.nslicer.rest.handlers


import com.example.nslicer.models.Field
import com.example.nslicer.utils.UNDERTOW_HELPERS
import io.undertow.server.{HttpServerExchange, HttpHandler}
import org.apache.commons.io.IOUtils
import spray.json.{JsObject, JsString}
import spray.json._

/**
  * Created by vishnu on 21/5/16.
  */
class FieldHandler extends HttpHandler {

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
            case "new" => FieldHandler.createFieldHandler(requestJson)
            case "get" => FieldHandler.getFieldHandler(requestJson)
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

object FieldHandler {
  def createFieldHandler(requestJson: JsObject): (Int, JsObject) = {
    if (requestJson.getFields("data").nonEmpty) {
      var field = Field.fromJson(requestJson.getFields("data").head.asJsObject)

      field = Field.getField(Field.saveToDb(field))

      if (field != null) {
        (200, JsObject("status" -> JsString("ok"), "data" -> field.toJson))
      } else {
        (500, JsObject(Map("status" -> JsString("failed"), "message" -> JsString("Unable to save Please Try again"))))
      }
    } else (0, JsObject())
  }

  def getFieldHandler(requestJson: JsObject): (Int, JsObject) = {
    val fieldId = if (requestJson.getFields("fieldId").nonEmpty)
      requestJson.getFields("fieldId").head.asInstanceOf[JsNumber].value.toLong
    else 0

    val field = Field.getField(fieldId)
    if (field != null)
      (200, JsObject("status" -> JsString("ok"), "data" -> field.toJson))
    else (0, null)
  }
}
