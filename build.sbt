name := """AEMComponentArchitectureGraph"""

version := "0.2"

scalaVersion := "2.11.8"

mainClass in Compile := Some("com.innovexa.AEMComponentArchitectureGraph")

// Change this to another test framework if you prefer
//libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"

libraryDependencies += "org.jsoup" % "jsoup" % "1.9.2"

// Uncomment to use Akka
//libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.11"

lazy val commonSettings = Seq(
  version := "0.2",
  organization := "com.innovexa",
  scalaVersion := "2.11.8"
)

lazy val AEMComponentArchitectureGraph = (project in file("app")).
  settings(commonSettings: _*).
  settings(
    // your settings here
)

test in assembly := {}
