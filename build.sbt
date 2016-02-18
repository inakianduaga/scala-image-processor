name := """scala-image"""

version := "1.0-SNAPSHOT"

// Ammonite Repl
libraryDependencies += "com.lihaoyi" % "ammonite-repl" % "0.5.4" % "test" cross CrossVersion.full

// Image processing
libraryDependencies += "com.sksamuel.scrimage" %% "scrimage-core" % "2.1.0"
libraryDependencies += "com.sksamuel.scrimage" %% "scrimage-io-extra" % "2.1.0"
libraryDependencies += "com.sksamuel.scrimage" %% "scrimage-filters" % "2.1.0"

initialCommands in (Test, console) := """ammonite.repl.Main.run("")"""

lazy val root = project.in(file(".")).enablePlugins(PlayScala)

