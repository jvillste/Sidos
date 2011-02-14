
package org.sidos.sbt.apigenerator


trait APIGeneratorPlugin extends Application
{
  
  lazy val hello = task { log.info("Hello World!"); None }

}