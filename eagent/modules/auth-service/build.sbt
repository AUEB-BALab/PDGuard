name := "auth"

organization := "org.eagent"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.7"

scalacOptions ++= Seq("-feature", "-deprecation", "-unchecked", "-language:reflectiveCalls")

javaOptions in Test += "-Dconfig.resource=application.test.conf"

javaOptions ++= Seq("-Xms512M",
  "-Xmx1024M",
  "-Xss1M",
  "-XX:+CMSClassUnloadingEnabled",
  "-Dplay.http.sslengineprovider=org.pdguard.eagent.ssl.CustomSSLEngineProvider",
  "-Dhttps.port=9443",
  "-Djavax.net.ssl.trustStore=/../../certs/agent.keystore",
  "-Djavax.net.ssl.trustStorePassword=serverpassword",
  "-Dplay.ssl.needClientAuth=true",
  "-Dconfig.resource=application.conf")

libraryDependencies ++= Seq(evolutions, jdbc, cache, ws, javaJdbc)
