package com.example.nslicer.rest


import com.example.nslicer.rest.handlers.{FieldHandler, DataSourceHandler, DefaultHandler}
import io.undertow.server.handlers.PathHandler

/**
  * Created by vishnu on 21/5/16.
  */
class Routes {

  def getAllHandlers = new PathHandler()
    .addExactPath("/", new DefaultHandler)
    .addPrefixPath("api", apiHandler)


  def apiHandler = new PathHandler()
    .addExactPath("dataSource", new DataSourceHandler)
    .addExactPath("fields", new FieldHandler)


}
