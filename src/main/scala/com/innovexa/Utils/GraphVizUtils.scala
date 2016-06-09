package com.innovexa.Utils

import scala.collection.mutable.ListBuffer

class GraphVizUtils {
}

object GraphVizUtils {
  def getDotFormattedStringUsingListOfDependantComponents(listOfDependantComponents: List[(String, String)]):String = {
    var stringListBuffer = new ListBuffer[String]()
    stringListBuffer += "digraph { rankdir=LR; "
    listOfDependantComponents.foreach(componentAndDependancy => {
      stringListBuffer += "\"" + componentAndDependancy._1 + "\" -> \"" + componentAndDependancy._2 + "\"; "
    })
    stringListBuffer += "}"

    stringListBuffer.toList.mkString("")
  }
}
