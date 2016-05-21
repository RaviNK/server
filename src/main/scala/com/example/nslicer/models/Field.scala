package com.example.nslicer.models

import java.sql.ResultSet

import com.example.nslicer.BootStrapServer.mysqlClient
import org.slf4j.LoggerFactory
import spray.json.{JsNumber, JsString, JsObject}

/**
  * Created by vishnu on 21/5/16.
  */
class Field(
             val fieldId: Long = 0,
             val sourceId: Long,
             val fieldName: String,
             val dataType: String, //double,long,string
             val valueType: String, // dimension,metrics,time
             val properties: Map[String, String] = Map()
           ) {

  def toJson: JsObject = JsObject(properties.map(i => i._1 -> JsString(i._2)) ++ Map(
    "fieldId" -> JsNumber(fieldId),
    "sourceId" -> JsNumber(sourceId),
    "fieldName" -> JsString(fieldName),
    "dataType" -> JsString(dataType),
    "valueType" -> JsString(valueType)
  ))

}

object Field {
  val LOG = LoggerFactory.getLogger(this.getClass)

  private val listOfCols = Array("fieldId", "fieldName", "dataType", "valueType", "sourceId")

  import spray.json._

  def fromJson(jsObject: JsObject): Field = {

    val properties = jsObject.fields.filter(i => listOfCols.contains(i._1)).map(i => i._1 -> i._2.asInstanceOf[JsString].value)

    val fieldId = if (jsObject.getFields("fieldId").nonEmpty)
      jsObject.getFields("fieldId").head.asInstanceOf[JsNumber].value.toLong
    else
      0

    val sourceId = if (jsObject.getFields("sourceId").nonEmpty)
      jsObject.getFields("sourceId").head.asInstanceOf[JsNumber].value.toLong
    else
      0

    val fieldName = if (jsObject.getFields("fieldName").nonEmpty)
      jsObject.getFields("fieldName").head.asInstanceOf[JsString].value
    else
      null

    val dataType = if (jsObject.getFields("dataType").nonEmpty)
      jsObject.getFields("dataType").head.asInstanceOf[JsString].value
    else
      null

    val valueType = if (jsObject.getFields("valueType").nonEmpty)
      jsObject.getFields("valueType").head.asInstanceOf[JsString].value
    else
      null


    if (dataType != null && valueType != null && fieldName != null && sourceId != 0)
      new Field(
        fieldId = fieldId,
        sourceId = sourceId,
        fieldName = fieldName,
        dataType = dataType,
        valueType = valueType,
        properties = properties
      )
    else
      null

  }

  def getField(fieldId: Long) = {

    val resultSet = mysqlClient.getResultSet("select * from fields where fieldId=" + fieldId)

    if (resultSet.next()) getFromResultSet(resultSet) else null
  }

  def getFromResultSet(resultSet: ResultSet): Field = {
    try {
      val fieldId = resultSet.getLong("fieldId")
      val sourceId = resultSet.getLong("sourceId")
      val fieldName = resultSet.getString("fieldName")
      val dataType = resultSet.getString("dataType")
      val valueType = resultSet.getString("valueType")
      val propertiesString = resultSet.getString("properties")

      val properties = propertiesString.parseJson.asJsObject.fields.map(f => {

        f._1 -> f._2.asInstanceOf[JsString].value
      })

      new Field(
        fieldId = fieldId,
        sourceId = sourceId,
        fieldName = fieldName,
        dataType = dataType,
        valueType = valueType,
        properties = properties
      )
    } catch {
      case e: Exception => LOG.error("Error while reading field ", e)
        null
    }
  }

  def saveToDb(field: Field): Long = {
    if (field.fieldId == 0) mysqlClient.insert("fields", Map(
      "sourceId" -> field.sourceId,
      "fieldName" -> field.fieldName,
      "dataType" -> field.dataType,
      "valueType" -> field.valueType,
      "properties" -> JsObject(field.properties.map(i => i._1 -> JsString(i._2))).toString
    ))
    else mysqlClient.insert("fields", Map(
      "fieldId" -> field.fieldId,
      "sourceId" -> field.sourceId,
      "fieldName" -> field.fieldName,
      "dataType" -> field.dataType,
      "valueType" -> field.valueType,
      "properties" -> JsObject(field.properties.map(i => i._1 -> JsString(i._2))).toString
    ))
  }

  def getFieldsForSource(sourceId: Long): Array[Field] = {
    val resultSet = mysqlClient.getResultSet("select * from fields where sourceId=" + sourceId)
    val buf = scala.collection.mutable.ListBuffer.empty[Field]
    while (resultSet.next) {
      buf += getFromResultSet(resultSet)
    }

    buf.filter(_ != null).toArray
  }
}
