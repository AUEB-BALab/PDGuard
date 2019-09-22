name := "common"

organization := "org.eagent"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.7"

scalacOptions ++= Seq("-feature", "-deprecation", "-unchecked", "-language:reflectiveCalls")

javaOptions in Test += s"-Dconfig.resource=application.test.conf"

javaOptions in run += "-Dconfig.resource=application.conf"

libraryDependencies ++= Seq(evolutions, jdbc, cache, ws, javaJdbc,
  "org.mockito" % "mockito-core" % "2.0.31-beta", "junit" % "junit" % "4.11",
  "org.powermock" % "powermock-api-mockito" % "1.6.3",
  "org.powermock" % "powermock-module-junit4" % "1.6.3")
