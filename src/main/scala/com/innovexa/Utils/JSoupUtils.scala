package com.innovexa.Utils

import java.io.File

import org.jsoup.Jsoup

import scala.collection.immutable.HashMap
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class JSoupUtils {
  protected def getSlingResourceSuperTypeFromXMLFileContents(xmlFileContent: String):String = {
    val slingResourceSuperTypeAttributeRegex = """sling:resourceSuperType="(.*?)"""".r
    slingResourceSuperTypeAttributeRegex
      .findFirstMatchIn(xmlFileContent)
      .map(_ group 1)
      .getOrElse("MEGASuperType")
  }

  protected def getComponentTitleFromXMLFileContents(xmlFileContent: String):String = {
    val slingResourceSuperTypeAttributeRegex = """jcr:title="(.*?)"""".r
    slingResourceSuperTypeAttributeRegex
      .findFirstMatchIn(xmlFileContent)
      .map(_ group 1)
      .getOrElse("<NoTitle>")
  }

  protected def getComponentJCRPathFromFilePath(componentXMLFilePath: String):String = {
    val regexForJCRPathFromFilePath = """jcr_root[\/\\]apps[\/\\](.*?components[\/\\]content[\/\\].*?)[\/\\]""".r
    regexForJCRPathFromFilePath
      .findFirstMatchIn(componentXMLFilePath)
      .map(_ group 1)
      .getOrElse("Error")
  }

  protected def getComponentNameFromPath(pathToHtmlFile: String):String = {
    val splitPath:Array[String] = pathToHtmlFile.split("/")
    val indexOfSecondLastElement = splitPath.length - 2
    splitPath(indexOfSecondLastElement)
  }
}

object JSoupUtils extends JSoupUtils{
  def getListOfDependantComponents(componentStructures: List[File]):List[(String, String)] = {
    val SLY_RESOURCE_TAG_CSS_SELECTOR = "div[data-sly-resource]"
    val SLY_ATTRIBUTE = "data-sly-resource"
    val regexForResourceType = "resourceType='(.*?)'".r
    componentStructures
      .map(componentHtmlFile => {
        (Jsoup.parse(componentHtmlFile, "UTF-8"), getComponentNameFromPath(componentHtmlFile.getAbsolutePath))
      })
      .map(documentHtmlFilenameTuple => {
        (documentHtmlFilenameTuple._1.select(SLY_RESOURCE_TAG_CSS_SELECTOR), documentHtmlFilenameTuple._2)
      })
      .filter(elementsAndHtmlFilenameTuple => {
        !elementsAndHtmlFilenameTuple._1.isEmpty
      })
      .flatMap(elementsAndHtmlFilenameTuple => {
        if (elementsAndHtmlFilenameTuple._1.size() > 1) {
          val returnList = new ListBuffer[(String, String)]()
          for (i <- 0 until elementsAndHtmlFilenameTuple._1.size) {
            returnList += new Tuple2(elementsAndHtmlFilenameTuple._1.get(i).attr(SLY_ATTRIBUTE),
              elementsAndHtmlFilenameTuple._2)
          }
          returnList.toList
        } else {
          List((elementsAndHtmlFilenameTuple._1.first().attr(SLY_ATTRIBUTE), elementsAndHtmlFilenameTuple._2))
        }
      })
      .map(elementsAndHtmlFileTuple => {
        val resourceTypeAttribute =
          regexForResourceType
          .findFirstMatchIn(elementsAndHtmlFileTuple._1)
          .map(_ group 1)
          .getOrElse("")
        (resourceTypeAttribute, elementsAndHtmlFileTuple._2)
      })
      .map(elementAndHtmlFileTuple => {
        (elementAndHtmlFileTuple._2, elementAndHtmlFileTuple._1)
      })
  }

  def getListOfInheritedComponents(componentXMLFiles: List[File]):List[(String, String)] = {
    componentXMLFiles
      .map(componentXMLFile => {
        val xmlFileContents = scala.io.Source.fromFile(componentXMLFile.getAbsolutePath, "utf-8").getLines.mkString
        (getSlingResourceSuperTypeFromXMLFileContents(xmlFileContents),
          getComponentJCRPathFromFilePath(componentXMLFile.getAbsolutePath))
      })
  }

  def getHashMapOfComponentPathToTitle(componentXMLFiles: List[File]):HashMap[String, String] = {
    var componentPathMapToTitle = new mutable.HashMap[String, String]()
    componentXMLFiles
      .foreach(componentXMLFile => {
        val xmlFileContents = scala.io.Source.fromFile(componentXMLFile.getAbsolutePath, "utf-8").getLines.mkString
        componentPathMapToTitle +=
          (getComponentJCRPathFromFilePath(componentXMLFile.getAbsolutePath)
            ->
            getComponentTitleFromXMLFileContents(xmlFileContents))
      })
    HashMap(componentPathMapToTitle.toSeq:_*)
  }
}
