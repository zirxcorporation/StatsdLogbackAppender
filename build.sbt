name := "StatsdLogbackAppender"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.7"

organization := "org.zirx"

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.1.3",
  "com.timgroup" % "java-statsd-client" % "3.0.1"
)

crossPaths := false