scalaVersion := "2.10.6"
resolvers ++= Seq(
  "scct-github-repository" at "http://mtkopone.github.com/scct/maven-repo"
)

libraryDependencies +=
  "edu.berkeley.cs" %% "chisel" % "2.2.38"
