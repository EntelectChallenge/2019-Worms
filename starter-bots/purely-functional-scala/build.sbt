lazy val root = (project in file(".")).
  settings(
    name := "purely-functional-scala-starter-bot",
    version := "1.0",
    scalaVersion := "2.12.8"
  )

libraryDependencies += "org.scalaz" %% "scalaz-zio" % "1.0-RC4"
libraryDependencies += "org.typelevel" %% "cats-core" % "1.6.0"

val circeVersion = "0.10.0"
libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

// Assembly plugin settings borrowed from existing Scala bot
assemblyJarName in assembly := "scala-sample-bot-jar-with-dependencies.jar"

assemblyMergeStrategy in assembly := {
  {
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case x => MergeStrategy.first
  }
}
