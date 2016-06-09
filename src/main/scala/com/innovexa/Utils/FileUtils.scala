package com.innovexa.Utils

import java.io.File

import scala.util.matching.Regex

class FileUtils {
  protected def removeSlashAtEndOfFilename(directory: String):String = {
    directory.replaceFirst("/$", "")
  }

  protected def getListOfFilesInFirstSubdirectoryWithRegex(f: File, regex: Regex): List[File] = {
    val firstSubDirectories = f.listFiles.toList.filter(_.isDirectory)
    firstSubDirectories.flatMap(subdirectory => {
      subdirectory.listFiles.filter(filename => regex.findFirstIn(filename.getName).isDefined)
    })
  }
}

object FileUtils extends FileUtils{
  def getListOfAllComponentHTMLFilesFromProject(directory: String):List[File] = {
    val step1 = removeSlashAtEndOfFilename(directory)

    val directoryFile = new File(step1)
    if (directoryFile.exists && directoryFile.isDirectory) {
      getListOfFilesInFirstSubdirectoryWithRegex(directoryFile, new Regex(""".*\.html$"""))
    } else {
      println("First argument either is not a directory, or doens't exist")
      println(directoryFile.getAbsolutePath)
      System.exit(1)
      List[File]()
    }
  }

  def getListOfAllComponentContentXMLFilesFromProject(directory: String):List[File] = {
    val step1 = removeSlashAtEndOfFilename(directory)

    val directoryFile = new File(step1)
    if (directoryFile.exists && directoryFile.isDirectory) {
      getListOfFilesInFirstSubdirectoryWithRegex(directoryFile, new Regex("""\.content\.xml$"""))
    } else {
      println("First argument either is not a directory, or doens't exist")
      println(directoryFile.getAbsolutePath)
      System.exit(1)
      List[File]()
    }
  }
}
