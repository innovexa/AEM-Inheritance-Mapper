package com.innovexa

import java.io.ByteArrayInputStream

import com.innovexa.Utils.{FileUtils, GraphVizUtils, JSoupUtils}

import scala.collection.immutable.HashMap
import scala.sys.process._

object AEMComponentArchitectureGraph {
  def main(args: Array[String]): Unit = {
    // Sanity check
    if (args.headOption.getOrElse("").isEmpty) {
      printHelpAndExit()
    }

    //buildCompositionGraph(args.head)

    buildInheritanceGraph(args.head)

  }

  def buildCompositionGraph(AEMDirectory: String):Unit = {
    val fileList = FileUtils.getListOfAllComponentHTMLFilesFromProject(AEMDirectory)
    val graphVizOptions = Some(
      """labelloc=t; label="AEM Component Architecture Graph - Composition"; fontsize=30;""")

    val elementsList = JSoupUtils.getListOfDependantComponents(fileList)

    val completeDotFormattedString =
      GraphVizUtils.getCompositionDotFormattedString(elementsList, graphVizOptions)

    val baInputStream = new ByteArrayInputStream(completeDotFormattedString.getBytes("UTF-8"))
    val outputFilename = "component_composition_graph.png"
    val out = (s"dot -Tpng -o ${outputFilename}" #< baInputStream).!
    if(out == 0) {
      println(s"Successfully created ${outputFilename} in local directory")
    }else {
      println(s"Error creating ${outputFilename}, run again with --verbose for more information")
      System.exit(1)
    }
  }

  def buildInheritanceGraph(AEMDirectory: String):Unit = {
    val fileList = FileUtils.getListOfAllComponentContentXMLFilesFromProject(AEMDirectory)
    val graphVizOptions = Some(
      """labelloc=t; label="AEM Component Architecture Graph - Inheritance"; fontsize=30;""")

    val components = JSoupUtils.getListOfInheritanceComponentObjects(fileList)

    val completeDotFormattedString =
      GraphVizUtils.getInheritanceDotFormattedString(components, graphVizOptions)
    println(completeDotFormattedString)


    val baInputStream = new ByteArrayInputStream(completeDotFormattedString.getBytes("UTF-8"))
    val outputFilename = "component_inheritance_graph.png"
    val out = (s"dot -Tpng -o ${outputFilename}" #< baInputStream).!
    if(out == 0) {
      println(s"Successfully created ${outputFilename} in local directory")
    }else {
      println(s"Error creating ${outputFilename}, run again with --verbose for more information")
      System.exit(1)
    }
  }

  def printHelpAndExit() = {
    println ("No arguments found. First argument must be the directory of the AEM components repository")
    System.exit(0)
  }
}
