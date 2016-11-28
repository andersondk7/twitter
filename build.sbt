import sbt._

name := "twitter"

organization := "org.aea"

scalaVersion := "2.11.8"

version := "1.0.0-SNAPSHOT"

scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation")

fork in Test := true

fork in IntegrationTest := true

parallelExecution := true

parallelExecution in Test := true

parallelExecution in IntegrationTest := true

libraryDependencies ++= Seq( cache , ws )

libraryDependencies += "org.specs2" %% "specs2-core" % "3.8.4" % "test"

libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.5"

libraryDependencies += "joda-time" % "joda-time" % "2.4"

libraryDependencies += "org.joda" % "joda-convert" % "1.8.1"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.2" % "test"

libraryDependencies += "org.twitter4j" % "twitter4j-stream" % "4.0.5"


enablePlugins(PlayScala)
