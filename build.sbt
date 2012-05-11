organization := "com.bimbr"

name := "clisson-tests"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.9.1"

resolvers ++= Seq(
  "Typesafe Repository"           at "http://repo.typesafe.com/typesafe/releases/",
  "Typesafe Snapshots Repository" at "http://repo.typesafe.com/typesafe/snapshots/"
)

libraryDependencies ++= Seq(
  "ch.qos.logback"             % "logback-classic"       % "1.0.2"          % "test",
  "com.bimbr"                  % "clisson-client"        % "0.3.0"          % "test",  
  "com.typesafe"               % "config"                % "0.4.0"          % "test",  
  "junit"                      % "junit"                 % "4.10"           % "test", 
  "log4j"                      % "log4j"                 % "1.2.16"         % "test",
  "org.slf4j"                  % "slf4j-api"             % "1.6.4"          % "test",
  "org.specs2"                %% "specs2"                % "1.8.2"          % "test"
)

scalacOptions += "-deprecation"

parallelExecution in Test := false

EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource
