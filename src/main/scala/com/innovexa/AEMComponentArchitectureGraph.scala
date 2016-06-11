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

    buildCompositionGraph(args.head)

    buildInheritanceGraph(args.head)

  }

  def buildCompositionGraph(AEMDirectory: String):Unit = {
    val fileList = FileUtils.getListOfAllComponentHTMLFilesFromProject(AEMDirectory)
    val graphVizOptions = Some(
      """labelloc=t; label="AEM Architecture Graph - Composition"; fontsize=30;""")

    val elementsList = JSoupUtils.getListOfDependantComponents(fileList)

    val completeDotFormattedString =
      GraphVizUtils.getCompositionDotFormattedString(elementsList, graphVizOptions)

    val baInputStream = new ByteArrayInputStream(completeDotFormattedString.getBytes("UTF-8"))
    val out = ("dot -Tpng -o composition_graph.png" #< baInputStream).!
    if(out == 0) {
      println("Successfully created composition_graph.png in local directory")
    }else {
      println("Error creating compositionGraph.png")
      System.exit(1)
    }
  }

  def buildInheritanceGraph(AEMDirectory: String):Unit = {
    val fileList = FileUtils.getListOfAllComponentContentXMLFilesFromProject(AEMDirectory)
    val graphVizOptions = Some(
      """labelloc=t; label="AEM Architecture Graph - Inheritance"; fontsize=30;""")

    val elementsList = JSoupUtils.getListOfInheritedComponents(fileList)

    val componentPathToTitle = JSoupUtils.getHashMapOfComponentPathToTitle(fileList)

    val completeDotFormattedString =
      GraphVizUtils.getInheritanceDotFormattedString(
        elementsList,
        componentPathToTitle,
        graphVizOptions)


    val baInputStream = new ByteArrayInputStream(completeDotFormattedString.getBytes("UTF-8"))
    val out = ("dot -Tpng -o inheritance_graph.png" #< baInputStream).!
    if(out == 0) {
      println("Successfully created inheritance_graph.png in local directory")
    }else {
      println("Error creating inheritance_graph.png")
      System.exit(1)
    }
  }

  def printHelpAndExit() = {
    println ("No arguments found. First argument must be the directory of the AEM components repository")
    System.exit(0)
  }
}
