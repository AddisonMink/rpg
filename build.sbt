enablePlugins(ScalaJSPlugin)

name := "jrpg"
version := "0.1.0-SNAPSHOT"
scalaVersion := "3.2.0"
resolvers += Resolver.mavenLocal
libraryDependencies += "org.typelevel" %%% "cats-core" % "2.8.0"
libraryDependencies += "org.typelevel" %%% "cats-mtl" % "1.2.1"
libraryDependencies += "com.github.julien-truffaut" %%% "monocle-core" % "3.0.0-M6"
libraryDependencies += "org.scalatest" %%% "scalatest" % "3.2.10" % Test
libraryDependencies += "default" %%% "canvas-ui" % "0.2.0-SNAPSHOT"

scalaJSUseMainModuleInitializer := true
