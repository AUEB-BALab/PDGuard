name := "web-ui"

organization := "org.eagent"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.7"

scalacOptions ++= Seq("-feature", "-deprecation", "-unchecked", "-language:reflectiveCalls")

javaOptions in Test += "-Dconfig.resource=application.test.conf"

javaOptions ++= Seq("-Xms512M",
  "-Xmx1024M",
  "-Xss2M",
  "-XX:+CMSClassUnloadingEnabled",
  "-Dhttps.port=9443",
  "-Djavax.net.ssl.trustStore=/../../certs/agent.keystore",
  "-Djavax.net.ssl.trustStorePassword=serverpassword",
  "-Dplay.ssl.needClientAuth=false",
  "-Dconfig.resource=application.conf")

libraryDependencies ++= Seq(evolutions, jdbc, cache, ws, javaJdbc,
  "org.webjars" % "bootstrap" % "3.3.4")
