import sbt._

class SidosParentProject(info: ProjectInfo) extends ParentProject(info) with IdeaProject
{

  lazy val juvi = project("juvi", "juvi", new DefaultProject(_) with IdeaProject with ModelGenerator)
  lazy val sirunsivutProject = project("fi.sirunsivut.project", "fi.sirunsivut.project", new DefaultProject(_) with IdeaProject, sidos)
  lazy val sidos = project("org.sidos", "org.sidos", new SidosProject(_), juvi)
  lazy val model = project("org.sidos.model")
  lazy val generator = project("org.sidos.sbt.apigenerator",model)

  class SidosProject(info: ProjectInfo) extends DefaultProject(info) with IdeaProject with ModelGenerator
  {
    val scalaTools = "Scala-Tools" at "http://scala-tools.org/repo-releases"
	  val akka = "Akka Maven2 Repository" at "http://www.scalablesolutions.se/akka/repository/"
	  val multiverse = "Multiverse Maven2 Repository" at "http://multiverse.googlecode.com/svn/maven-repository/releases/"
	  val guiceyFruit = "GuiceyFruit Maven2 Repository" at "http://guiceyfruit.googlecode.com/svn/repo/releases/"
	  val jboss = "JBoss Maven2 Repository" at "https://repository.jboss.org/nexus/content/groups/public/"
  
	  val akkaActor = "se.scalablesolutions.akka" % "akka-actor" % "1.0-RC3" withSources ()
	  val h2 = "com.h2database" % "h2" % "1.2.144" withSources ()
	  val scalatest = "org.scalatest" % "scalatest" % "1.2" withSources ()

  }


  trait ModelGenerator extends Project {
    lazy val generate = task {

      log.info(outputPath.toString);
      None
    }
  }

}
