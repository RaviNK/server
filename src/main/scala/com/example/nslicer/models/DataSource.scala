package com.example.nslicer.models

import java.sql.ResultSet

import com.example.nslicer.BootStrapServer._
import org.slf4j.LoggerFactory
import spray.json.{JsNumber, JsString, JsObject}

/**
  * Created by vishnu on 21/5/16.
  */
class DataSource(
                  val sourceId: Long = 0,
                  val sourceName: String,
                  val sourceType: String, // csv or mysql
                  val sourcePath: String // connectionString
                ) {

  def toJson: JsObject = JsObject(
    "sourceId" -> JsNumber(sourceId),
    "sourceName" -> JsString(sourceName),
    "sourceType" -> JsString(sourceType),
    "sourcePath" -> JsString(sourcePath)
  )

}


object DataSource {

  val LOG = LoggerFactory.getLogger(this.getClass)

  def fromJson(jsObject: JsObject): DataSource = {

    val sourceName = if (jsObject.getFields("sourceName").nonEmpty)
      jsObject.getFields("sourceName").head.asInstanceOf[JsString].value
    else
      null
    val sourceType = if (jsObject.getFields("sourceType").nonEmpty)
      jsObject.getFields("sourceType").head.asInstanceOf[JsString].value
    else
      null
    val sourcePath = if (jsObject.getFields("sourcePath").nonEmpty)
      jsObject.getFields("sourcePath").head.asInstanceOf[JsString].value
    else
      null
    val sourceId = if (jsObject.getFields("sourceId").nonEmpty)
      jsObject.getFields("sourceId").head.asInstanceOf[JsNumber].value.toLong
    else
      0

    if (sourceName != null && sourceType != null && sourcePath != null) {
      new DataSource(
        sourceId = sourceId,
        sourceName = sourceName,
        sourceType = sourceType,
        sourcePath = sourcePath
      )
    } else null
  }

  def getDataSource(sourceId: Long) = {

    val resultSet = mysqlClient.getResultSet("select * from sources where sourceId=" + sourceId)

    if (resultSet.next()) getFromResultSet(resultSet) else null
  }

  def getFromResultSet(resultSet: ResultSet) = {
    try {
      val sourceId = resultSet.getLong("sourceId")
      val sourceName = resultSet.getString("sourceName")
      val sourceType = resultSet.getString("sourceType")
      val sourcePath = resultSet.getString("sourcePath")

      new DataSource(
        sourceId = sourceId,
        sourceName = sourceName,
        sourceType = sourceType,
        sourcePath = sourcePath
      )
    } catch {
      case e: Exception => LOG.error("Error while reading field ", e)
        null
    }
  }

  def saveToDb(dataSource: DataSource): Long = {
    if (dataSource.sourceId == 0) mysqlClient.insert("sources", Map(
      "sourceName" -> dataSource.sourceName,
      "sourceType" -> dataSource.sourceType,
      "sourcePath" -> dataSource.sourcePath
    ))
    else mysqlClient.insert("fields", Map(
      "sourceId" -> dataSource.sourceId,
      "sourceName" -> dataSource.sourceName,
      "sourceType" -> dataSource.sourceType,
      "sourcePath" -> dataSource.sourcePath
    ))
  }

  def prepareSource(source: DataSource): Unit = {
    val fields: Array[Field] = Field.getFieldsForSource(source.sourceId)

    val (categorical, continuous) = fields.partition(_.valueType == "continuous")

    categorical.foreach(println)

  }

}
