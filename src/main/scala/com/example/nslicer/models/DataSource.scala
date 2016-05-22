package com.example.nslicer.models

import java.sql.ResultSet

import com.example.nslicer.BootStrapServer._
import org.slf4j.LoggerFactory
import spray.json._

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
    "sourcePath" -> JsString(sourcePath),
    "fields" -> JsArray(getFields.map(_.toJson).toVector)
  )

  def getFields: Array[Field] = Field.getFieldsForSource(sourceId)
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
    println("Getting dataSource")
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

  def prepareSource(source: DataSource): JsObject = {

    val fields: Array[Field] = Field.getFieldsForSource(source.sourceId)

    val (tmp, measures) = fields.partition(_.valueType.toLowerCase != "measure")

    val (dimensions, time) = tmp.partition(_.valueType.toLowerCase != "time")

    val dimensionSpec: JsObject = JsObject(
      "dimensions" -> JsArray(dimensions.map(i => JsString(i.fieldName)).toVector)
    )

    val timeSpec: JsObject = JsObject(
      "format" -> JsString("auto"),
      "column" -> JsString(time.head.fieldName)
    )

    val metricsSpec: JsArray = JsArray((measures.map(field => {
      JsObject(
        "type" -> JsString(if (field.dataType.toLowerCase == "double") "doubleSum" else "longSum"),
        "name" -> JsString(field.fieldName + "Sum"),
        "fieldName" -> JsString(field.fieldName)
      )
    }) :+ JsObject(
      "type" -> JsString("count"),
      "name" -> JsString("count")
    )).toVector)

    val parseSpec = JsObject(
      "timestampSpec" -> timeSpec,
      "dimensionsSpec" -> dimensionSpec,
      "format" -> JsString("json")
    )

    val parser = JsObject(
      "type" -> JsString("string"),
      "parseSpec" -> parseSpec
    )

    val granularitySpec = "{\"type\": \"uniform\",\"segmentGranularity\": \"hour\",\"queryGranularity\": \"none\"}".parseJson.asJsObject

    val dataSchema = JsObject(
      "dataSource" -> JsString(source.sourceName),
      "parser" -> parser,
      "granularitySpec" -> granularitySpec,
      "metricsSpec" -> metricsSpec
    )

    val ioConfig = JsObject("type" -> JsString("realtime"))
    val tuningConfig = "{\"type\":\"realtime\",\"maxRowsInMemory\":\"100000\",\"intermediatePersistPeriod\":\"PT10M\",\"windowPeriod\":\"PT10M\"}".parseJson.asJsObject
    val spec = JsObject(
      "ioConfig" -> ioConfig,
      "tuningConfig" -> tuningConfig,
      "dataSchema" -> dataSchema
    )

    val properties = "{\"zookeeper.connect\":\"localhost\",\"druid.discovery.curator.path\":\"/druid/discovery\",\"druid.selectors.indexing.serviceName\":\"druid/overlord\",\"commit.periodMillis\":\"15000\",\"consumer.numThreads\":\"2\",\"kafka.zookeeper.connect\":\"localhost\",\"kafka.group.id\":\"tranquility-kafka\"}".parseJson.asJsObject

    val finalJson = JsObject(
      "type" -> JsString("index_realtime"),
      "spec" -> spec,
      "properties" -> properties
    )
    finalJson
  }

}
