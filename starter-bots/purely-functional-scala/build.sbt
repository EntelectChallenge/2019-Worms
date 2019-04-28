libraryDependencies += "org.scalaz" %% "scalaz-zio" % "1.0-RC4"
libraryDependencies += "org.typelevel" %% "cats-core" % "1.6.0"
val circeVersion = "0.10.0"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

assemblyJarName in assembly := "scala-sample-bot-jar-with-dependencies.jar"

// META-INF discarding
assemblyMergeStrategy in assembly := {
{
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}
}
