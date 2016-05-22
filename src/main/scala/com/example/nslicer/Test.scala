package com.example.nslicer

import com.example.nslicer.rest.handlers.DataSourceHandler

/**
  * Created by vishnu on 21/5/16.
  */
object Test {


  def main(args: Array[String]) {
    println("Starting")
    println(BootStrapServer.mysqlClient.host)
    testCreateJson()
    println("Exiting main")
  }


  def testCreateJson() = {

    import spray.json._
    val json =
      """
        |{
        |  "action": "new",
        |  "data": {
        |    "sourceName": "test",
        |    "sourceType": "csv",
        |    "sourcePath": ""
        |  }
        |}
      """.stripMargin.parseJson.asJsObject

    val source = DataSourceHandler.createDataSource(json)
    println(source._1 + "\n" + source._2.prettyPrint)
  }

}
