name := """eshop"""

version := "1.0-SNAPSHOT"

//enabling Ebean
lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(javaJdbc, cache, javaWs,
  "org.mockito" % "mockito-core" % "2.0.31-beta", "junit" % "junit" % "4.11",
  "org.powermock" % "powermock-api-mockito" % "1.6.3",
  "org.powermock" % "powermock-module-junit4" % "1.6.3")

javaOptions ++= Seq("-Dhttp.port=9043")

javaOptions in Test += s"-Dconfig.resource=test.conf"
