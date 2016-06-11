package com.innovexa

import java.io.ByteArrayInputStream

import com.innovexa.Utils.{FileUtils, GraphVizUtils, JSoupUtils}

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
    val components = JSoupUtils.getListOfDependantComponents(fileList)

    val completeDotFormattedString = GraphVizUtils.getCompositionDotFormattedString(components, None)

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

    val components = JSoupUtils.getListOfInheritanceComponentObjects(fileList)

    val completeDotFormattedString = GraphVizUtils.getInheritanceDotFormattedString(components, None)
    //println(completeDotFormattedString)


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
