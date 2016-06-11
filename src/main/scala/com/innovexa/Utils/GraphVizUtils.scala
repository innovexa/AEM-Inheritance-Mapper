package com.innovexa.Utils

import com.innovexa.Models.Component

import scala.collection.mutable.ListBuffer

class GraphVizUtils {
  protected def getComponentVertexFormatted(component: Component):String = {
    val componentTitle = component.title.getOrElse("<No Title>")
    val componentGroup = component.componentGroup.getOrElse("<No Group>")
    s""""${component.jcrPath}"[
        shape = none
        label = <<table border="1" cellspacing="0">
          <tr><td border="0"><font point-size="20">${componentTitle}</font></td></tr>
          <tr><td border="0"><font point-size="15">${component.jcrPath}</font></td></tr>
          <tr><td border="0"><font point-size="15">${componentGroup}</font></td></tr>
          </table>>
       ];""" + "\n"
  }
}

object GraphVizUtils extends GraphVizUtils{
  def getInheritanceDotFormattedString(listOfDependantComponents: List[Component],
                                       receivedGraphVizOptions: Option[String]):String = {
    var stringListBuffer = new ListBuffer[String]()
    val graphVizOptions = receivedGraphVizOptions.getOrElse("")
    val DEFAULT_SUPERTYPE = "MegaSuperType"
    stringListBuffer +=
    s"""digraph {
      rankdir = LR
      ${graphVizOptions}
      ${DEFAULT_SUPERTYPE}[
        label = "${DEFAULT_SUPERTYPE}"
        fontsize = 20
      ];
    """

    listOfDependantComponents.foreach(component => {
      val vertexPropertiesInDotFormat = getComponentVertexFormatted(component)
      val parentVertex = component.resourceSuperType.getOrElse(DEFAULT_SUPERTYPE)
      stringListBuffer +=
        vertexPropertiesInDotFormat +
        s""""${parentVertex}" -> "${component.jcrPath}";
         """
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
