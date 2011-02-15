import sbt._
class Project(info: ProjectInfo) extends DefaultProject(info) with IdeaProject
{
  val scalaTools = "Scala-Tools" at "http://scala-tools.org/repo-releases"
  val scalasti = "org.clapper" % "scalasti_2.8.1" % "0.5.1"
	
  //project name
  override val artifactID = "APIGenerator"

  //program entry point
  override def mainClass: Option[String] = Some("org.sidos.model.apigenerator.APIGenerator")

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