package com.innovexa.Utils

import com.innovexa.Models.Component

import scala.collection.mutable.ListBuffer

class GraphVizUtils {
  protected def getComponentVertexFormatted(component: Component,
                                            componentGroupToColorMap: Map[String, String]):String = {
    val componentTitle = component.title.getOrElse("<No Title>")
    val componentGroup = component.componentGroup.getOrElse("<No Group>")
    val backgroundColorForGroupName = componentGroupToColorMap.getOrElse(componentGroup, "gray")
    s""""${component.jcrPath}"[
        shape = none
        label = <<table border="1" cellspacing="0">
          <tr><td border="0"><font point-size="20">${componentTitle}</font></td></tr>
          <tr><td border="0"><font point-size="15">${component.jcrPath}</font></td></tr>
          <tr><td border="0" bgcolor="${backgroundColorForGroupName}"><font point-size="15">${componentGroup}</font></td></tr>
          </table>>
       ];""" + "\n"
  }

  protected def createMapOfComponentGroupsToColors(listOfComponents: List[Component]):Map[String, String] = {
    val uniqueComponentGroupNames = listOfComponents.flatMap(_.componentGroup).distinct
    val listOfColors = getListOfColors.take(uniqueComponentGroupNames.size).toList
    uniqueComponentGroupNames.zip(listOfColors).toMap
  }

  private def getListOfColors():Stream[String] = {
    // Will make a list like 1,2,3,4,4,4,4,4
    // Taken from Material Design Guidelines
    val definedColorList = List("#FF7043", "#66BB6A", "#42A5F5", "#FFCA28", "#26C6DA", "#D4E157","#7E57C2")
    val defaultGroupColorIfMoreGroupsThanListSize = "#BDBDBD"

    lazy val infiniteTailList:Stream[String] = List(defaultGroupColorIfMoreGroupsThanListSize).toStream #:::
      infiniteTailList

    definedColorList.toStream #::: infiniteTailList
  }
}

object GraphVizUtils extends GraphVizUtils{
  def getInheritanceDotFormattedString(listOfDependantComponents: List[Component],
                                       receivedGraphVizOptions: Option[String]):String = {
    var stringListBuffer = new ListBuffer[String]()
    val graphVizOptions = receivedGraphVizOptions.getOrElse("")
    val componentGroupToColorsMap = createMapOfComponentGroupsToColors(listOfDependantComponents)
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
      val vertexPropertiesInDotFormat = getComponentVertexFormatted(component, componentGroupToColorsMap)
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
