package com.innovexa.Utils

import scala.collection.immutable.HashMap
import scala.collection.mutable.ListBuffer

class GraphVizUtils {
  protected def getComponentVertexFormatted(dependantComponentPath: String,
                                            componentPathMapToTitle: HashMap[String, String]):String = {
    val componentTitle = componentPathMapToTitle.getOrElse(dependantComponentPath, "<No Title>")
    s""""${dependantComponentPath}"[label="${dependantComponentPath}\\n${componentTitle}"];""" + "\n"
  }
}

object GraphVizUtils extends GraphVizUtils{
  def getInheritanceDotFormattedString(listOfDependantComponents: List[(String, String)],
                                                          componentPathMapToTitle: HashMap[String, String],
                                                          graphVizOptions: Option[String]):String = {
    var stringListBuffer = new ListBuffer[String]()
    stringListBuffer += "digraph { rankdir=LR; " + graphVizOptions.getOrElse("") + "\n"
    listOfDependantComponents.foreach(componentAndDependancy => {
      val componentFormattedVertex = getComponentVertexFormatted(componentAndDependancy._2, componentPathMapToTitle)
      stringListBuffer +=
        componentFormattedVertex +
        s""""${componentAndDependancy._1}" -> "${componentAndDependancy._2}";""" + "\n"
    })
    stringListBuffer += "}"

    stringListBuffer.toList.mkString("")
  }

  def getCompositionDotFormattedString(listOfDependantComponents: List[(String, String)],
                                       graphVizOptions: Option[String]):String = {
    var stringListBuffer = new ListBuffer[String]()
    stringListBuffer += "digraph { rankdir=LR; " + graphVizOptions.getOrElse("") + "\n"
    listOfDependantComponents.foreach(componentAndDependancy => {
      stringListBuffer +=
          s""""${componentAndDependancy._1}" -> "${componentAndDependancy._2}";""" + "\n"
    })
    stringListBuffer += "}"

    stringListBuffer.toList.mkString("")
  }
}
