name := "web-reddit-scala-backend"

version := "1.0"

scalaVersion := "2.11.2"

resolvers ++= Seq(
  "Sonatype Releases"   at "http://oss.sonatype.org/content/repositories/releases",
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Spray Repository"    at "http://repo.spray.io/",
  "Spray Nightlies"     at "http://nightlies.spray.io/"
)

libraryDependencies ++= {
  val akkaVersion  = "2.3.6"
  val sprayVersion = "1.3.2"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "io.spray" %%  "spray-can" % sprayVersion,
    "io.spray" %%  "spray-routing" % sprayVersion,
    "io.spray" %%  "spray-client" % sprayVersion,
    "io.spray" %%  "spray-json" % "1.3.1"
  )
}