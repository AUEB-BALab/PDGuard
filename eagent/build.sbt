name := "eagent"

organization := "org.eagent"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.7"

lazy val common = (project in file("modules/common")).enablePlugins(PlayScala, PlayEbean)

lazy val web = (project in file("modules/web-ui")).enablePlugins(PlayScala).dependsOn(common)

lazy val auth = (project in file("modules/auth-service")).enablePlugins(PlayScala).dependsOn(common)

lazy val root = (project in file(".")).dependsOn(web, auth, common).enablePlugins(PlayScala, PlayEbean).aggregate(web, auth, common)

javaOptions ++= Seq("-Xms512M",
  "-Xmx1024M",
  "-Xss1M",
  "-XX:+CMSClassUnloadingEnabled",
  "-Dplay.http.sslengineprovider=ssl.CustomSSLEngineProvider",
  "-Dhttps.port=9443",
  "-Djavax.net.ssl.trustStore=/certs/agent.keystore",
  "-Djavax.net.ssl.trustStorePassword=serverpassword",
  "-Dplay.ssl.needClientAuth=false")

// Library dependencies of application
libraryDependencies ++= Seq(evolutions, jdbc, cache, ws, javaJdbc)
