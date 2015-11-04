name := "hystrixTests"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "io.reactivex"           %  "rxjava"              % "1.0.14", // libreria RXJava
  "com.netflix.hystrix"    %  "hystrix-core"        % "1.4.14",  // libreria del core de hystrix
  "com.netflix.hystrix"    %  "hystrix-metrics-event-stream" % "1.4.18", // libreria para metricas de hystrix
  "org.eclipse.jetty"      %  "jetty-server"        % "9.3.5.v20151012", // Libreria de jetty
  "org.eclipse.jetty"      %  "jetty-servlet"       % "9.3.5.v20151012"  // Libreria de jetty
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

