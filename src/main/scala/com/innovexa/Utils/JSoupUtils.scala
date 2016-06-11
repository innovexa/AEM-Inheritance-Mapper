package com.innovexa.Utils

import java.io.File

import com.innovexa.Models.Component
import org.jsoup.Jsoup

import scala.collection.immutable.HashMap
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.matching.Regex

class JSoupUtils {
  case class JCRPathNotFoundException(message: String) extends Exception(message)

  private def getFirstRegexMatchInStringWithDefault(contents: String, regex: Regex):Option[String] = {
    regex
      .findFirstMatchIn(contents)
      .map(_ group 1)
  }

  protected def getSlingResourceSuperTypeFromXMLFileContents(xmlFileContent: String):Option[String] = {
    getFirstRegexMatchInStringWithDefault(xmlFileContent, """sling:resourceSuperType="(.*?)"""".r)
  }

  protected def getComponentGroupFromXMLFileContents(xmlFileContent: String):Option[String] = {
    getFirstRegexMatchInStringWithDefault(xmlFileContent, """componentGroup="(.*?)"""".r)
  }

  protected def getComponentTitleFromXMLFileContents(xmlFileContent: String):Option[String] = {
    getFirstRegexMatchInStringWithDefault(xmlFileContent, """jcr:title="(.*?)"""".r)
  }

  protected def getComponentJCRPathFromFilePath(componentXMLFilePath: String):Option[String] = {
    getFirstRegexMatchInStringWithDefault(componentXMLFilePath,
      """jcr_root[\/\\]apps[\/\\](.*?components[\/\\]content[\/\\].*?)[\/\\]""".r)
  }

  protected def getComponentNameFromPath(pathToHtmlFile: String):String = {
    // Example: /hello/world/component/hello.xml
    // result: component
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

  def getListOfInheritanceComponentObjects(componentXMLFiles: List[File]):List[Component] = {
    componentXMLFiles
      .map(componentXMLFile => {
        val xmlFileContents = scala.io.Source.fromFile(componentXMLFile.getAbsolutePath, "utf-8").getLines.mkString

        val jcrPathForComponent =
          getComponentJCRPathFromFilePath(componentXMLFile.getAbsolutePath) match {
          case None =>
            throw new JCRPathNotFoundException("Could not get the JCR path from filename for file: " +
              componentXMLFile.getAbsolutePath)
          case Some(jcrPath) => jcrPath
        }

        val resourceSuperType = getSlingResourceSuperTypeFromXMLFileContents(xmlFileContents)
        val titleForComponent = getComponentTitleFromXMLFileContents(xmlFileContents)
        val componentGroupForComponent = getComponentGroupFromXMLFileContents(xmlFileContents)

        new Component(jcrPathForComponent, resourceSuperType, titleForComponent, componentGroupForComponent)
      })
  }

/*  def getHashMapOfComponentPathToTitle(componentXMLFiles: List[File]):HashMap[String, String] = {
    var componentPathMapToTitle = new mutable.HashMap[String, String]()
    componentXMLFiles
      .foreach(componentXMLFile => {
        val xmlFileContents = scala.io.Source.fromFile(componentXMLFile.getAbsolutePath, "utf-8").getLines.mkString
        componentPathMapToTitle +=
          (getComponentJCRPathFromFilePath(componentXMLFile.getAbsolutePath)
            ->

      })
    HashMap(componentPathMapToTitle.toSeq:_*)
  }*/
}
