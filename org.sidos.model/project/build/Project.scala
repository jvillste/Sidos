import sbt._
class Project(info: ProjectInfo) extends DefaultProject(info) with IdeaProject
{
    val scalaTools = "Scala-Tools" at "http://scala-tools.org/repo-releases"
    val scalasti = "org.clapper" % "scalasti_2.8.1" % "0.5.1"
}