name := "nlp"

version := "1.0"

scalaVersion := "2.10.5"

val log4jVersion = "1.2.17"
val stanfordVersion = "3.6.0"

libraryDependencies ++= Seq(
  "org.apache.opennlp" % "opennlp-tools" % "1.6.0",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.6.0",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.6.0" classifier "models",
  "com.google.protobuf" % "protobuf-java" % "2.6.1",
  "org.glassfish" % "javax.json" % "1.0.4",
  "log4j" % "log4j" % log4jVersion,
  "com.github.tototoshi" %% "scala-csv" % "1.3.3"
)



