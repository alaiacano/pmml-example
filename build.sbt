import AssemblyKeys._ // put this at the top of the file

assemblySettings

name := "pmml-example"

version := "0.0.1"

scalaVersion := "2.10.3"

val scaldingVersion = "0.9.1"

val pmmlVersion = "1.0.21"

resolvers ++= Seq(
  "Conjars" at "http://conjars.org/repo"
)

libraryDependencies ++= Seq(
  "com.twitter" % "scalding-core_2.10" % scaldingVersion,
  "com.twitter" % "scalding-args_2.10" % scaldingVersion,
  "org.jpmml" % "pmml-evaluator" % pmmlVersion,
  "org.jpmml" % "pmml-model" % pmmlVersion,
  "org.apache.hadoop" % "hadoop-core" % "0.20.2" % "provided"
)

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
  {
    case s if s.endsWith(".class") => MergeStrategy.last
    case s if s.endsWith("project.clj") => MergeStrategy.concat
    case s if s.endsWith(".html") => MergeStrategy.last
    case s if s.endsWith(".dtd") => MergeStrategy.last
    case s if s.endsWith(".xsd") => MergeStrategy.last
    case s if s.endsWith("pom.xml") => MergeStrategy.last
    case s if s.endsWith("pom.properties") => MergeStrategy.last
    case x => old(x)
  }
}
