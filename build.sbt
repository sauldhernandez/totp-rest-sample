lazy val root = (project in file(".")).enablePlugins(ZordonGenPlugin, AssemblyPlugin)

name := "googleAuthTest"

version := "1.0"

scalaVersion := "2.11.7"

resolvers += "jitpack.io" at "https://jitpack.io"

libraryDependencies ++= Seq(
  "com.warrenstrange" % "googleauth" % "0.5.0",
  "com.github.kenglxn.qrgen" % "javase" % "2.1.0"
)

zordonGenConfig := Seq(zordonGenConfig.value.head.copy(
  packageName = "com.googleAuthTest.rest",
  generateStubs = true
))