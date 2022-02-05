

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.1.0"

lazy val root = (project in file("."))
  .settings(
    name := "cats-effect-journey",
    idePackagePrefix := Some("org.arrnaux"),

    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % "3.3.5",
    )
  )
