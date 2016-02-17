resolvers ++= Seq(
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "Synergy-GB Snapshots" at "https://nexus.synergy-gb.com/nexus/content/repositories/snapshots/"
)

addSbtPlugin("com.synergygb.zordon" % "zordon-gen" % "1.0.0-SNAPSHOT")

addSbtPlugin("com.synergygb.zordon" % "zordon-assembly" % "1.0.0-SNAPSHOT")