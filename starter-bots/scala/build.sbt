lazy val root = (project in file(".")).
  settings(
    name := "scala-starter-bot",
    version := "1.0",
    scalaVersion := "2.12.8",
    mainClass in Compile := Some("za.co.entelect.challenge.Main")
  )

libraryDependencies ++= Seq(
  "com.beachape" %% "enumeratum-json4s"  % "1.5.14",
  "org.json4s"   %% "json4s-native"      % "3.6.5"
)

assemblyJarName in assembly := "scala-sample-bot-jar-with-dependencies.jar"

// META-INF discarding
assemblyMergeStrategy in assembly := {
{
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}
}