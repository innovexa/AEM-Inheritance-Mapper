name := """AEMComponentArchitectureGraph"""

version := "1.0"

scalaVersion := "2.11.7"

mainClass in Compile := Some("com.innovexa.AEMComponentArchitectureGraph")

// Change this to another test framework if you prefer
//libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"

libraryDependencies += "org.jsoup" % "jsoup" % "1.9.2"

// Uncomment to use Akka
//libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.11"

lazy val commonSettings = Seq(
  version := "1.0-SNAPSHOT",
  organization := "com.innovexa",
  scalaVersion := "2.11.7"
)

lazy val app = (project in file("app")).
  settings(commonSettings: _*).
  settings(
    // your settings here
)

test in assembly := {}
