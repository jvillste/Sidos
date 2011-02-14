import sbt._
class APIGeneratorPluginProject(info: ProjectInfo) extends DefaultProject(info) with IdeaProject with ProguardProject
{
    //val localIvy = "Local Ivy Repository" at new java.io.File(Path.userHome + "/.ivy2/local").toURI.toString
	//val localIvy = "Local Ivy Repository" at "file:///C:/Documents%20and%20Settings/Jukka/.ivy2/local"
	val model = "org.sidos" %% "org.sidos.model" % "1.0"
	
	//project name
  override val artifactID = "APIGeneratorApplication"

  //program entry point
  override def mainClass: Option[String] = Some("org.sidos.sbt.apigenerator.APIGeneratorApplication")

  //proguard
  override def proguardOptions = List(
    "-keepclasseswithmembers public class * { public static void main(java.lang.String[]); }",
    "-dontoptimize",
    "-dontobfuscate",
    proguardKeepLimitedSerializability,
    proguardKeepAllScala,
    "-keep interface scala.ScalaObject"
  )
  override def proguardInJars = Path.fromFile(scalaLibraryJar) +++ super.proguardInJars
}