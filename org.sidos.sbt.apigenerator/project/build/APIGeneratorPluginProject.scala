import sbt._
class APIGeneratorPluginProject(info: ProjectInfo) extends DefaultProject(info) with IdeaProject
{
    //val localIvy = "Local Ivy Repository" at new java.io.File(Path.userHome + "/.ivy2/local").toURI.toString
	//val localIvy = "Local Ivy Repository" at "file:///C:/Documents%20and%20Settings/Jukka/.ivy2/local"
	val model = "org.sidos" %% "org.sidos.model" % "1.0"
}