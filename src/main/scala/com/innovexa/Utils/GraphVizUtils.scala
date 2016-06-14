package com.innovexa.Utils

import java.io.File

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

  protected def getTitleOfGraph(title: String):String = {
    s"""
      labelloc = t
      label = <<table border="0" cellspacing="0">
        <tr><td border="0"><font point-size="30">${title}</font></td></tr>
        <tr><td border="0" href="https://github.com/innovexa/AEMComponentArchitectureGraph"><font point-size="15">
        |Created using https://github.com/innovexa/AEMComponentArchitectureGraph</font></td></tr>
        </table>>
    """.stripMargin
  }

  protected def getFoundationVertexDefinitions(listOfDependantComponents: List[Component]):String = {
    val uniqueListOfSuperTypes = listOfDependantComponents.flatMap(_.resourceSuperType).distinct
    val foundationSuperTypes = uniqueListOfSuperTypes.filter(superType => {
      superType.startsWith("wcm" + File.separator) || superType.startsWith("foundation" + File.separator)
    })
    val stringListBuffer = new ListBuffer[String]()
    stringListBuffer +=
      """Granite[
        fontsize = 20
        ]""" + "\n"

    foundationSuperTypes.foreach(superType => {
      stringListBuffer +=
        s""""${superType}"[
          label = "${superType}"
          fontsize = 20
         ];

         Granite -> "${superType}"
        """ + "\n"
    })
    stringListBuffer.mkString
  }
}

object GraphVizUtils extends GraphVizUtils{
  def getInheritanceDotFormattedString(listOfDependantComponents: List[Component],
                                       receivedGraphVizOptions: Option[String]):String = {
    var stringListBuffer = new ListBuffer[String]()
    val graphVizOptions = receivedGraphVizOptions.getOrElse("")
    val componentGroupToColorsMap = createMapOfComponentGroupsToColors(listOfDependantComponents)
    val titleLabel = getTitleOfGraph("AEM Component Architecture Graph - Inheritance")
    val foundationVertexProperties = getFoundationVertexDefinitions(listOfDependantComponents)
    val DEFAULT_SUPERTYPE = "MegaSuperType"

    stringListBuffer +=
    s"""digraph {
      rankdir = LR
      ${graphVizOptions}
      ${titleLabel}
      ${DEFAULT_SUPERTYPE}[
        label = "${DEFAULT_SUPERTYPE}"
        fontsize = 20
      ];
      "${DEFAULT_SUPERTYPE}" -> Granite
      ${foundationVertexProperties}
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

    stringListBuffer.toList.mkString
  }

  def getCompositionDotFormattedString(listOfDependantComponents: List[(String, String)],
                                       receivedGraphVizOptions: Option[String]):String = {
    var stringListBuffer = new ListBuffer[String]()
    val graphVizOptions = receivedGraphVizOptions.getOrElse("")
    val titleLabel = getTitleOfGraph("AEM Component Architecture Graph - Composition")

    stringListBuffer +=
    s"""digraph {
        rankdir = LR
        ${graphVizOptions}
        ${titleLabel}
    """

    listOfDependantComponents.foreach(componentAndDependancy => {
      stringListBuffer +=
          s""""${componentAndDependancy._1}" -> "${componentAndDependancy._2}";""" + "\n"
    })
    stringListBuffer += "}"

    stringListBuffer.toList.mkString("")
  }
}
