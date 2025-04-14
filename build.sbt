ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.5"

lazy val root = (project in file("."))
  .settings(
    name := "Stock Data Dashboard"
  )

libraryDependencies += "org.scalafx" % "scalafx_3" % "22.0.0-R33"
libraryDependencies += "org.json4s" %% "json4s-jackson" % "4.0.7"
libraryDependencies ++= Seq("com.typesafe.akka" %% "akka-actor" % "2.8.7", "com.typesafe.akka" %% "akka-stream" % "2.8.7")


