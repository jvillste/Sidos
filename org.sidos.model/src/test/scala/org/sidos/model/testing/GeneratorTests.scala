
package org.sidos.model.testing;

import org.scalatest.testng.TestNGSuite
import org.scalatest.matchers.ShouldMatchers
import org.testng.annotations.Test
import org.sidos.model.compiler.SidosCompiler

class GeneratorTests extends TestNGSuite with ShouldMatchers {
  @Test
  def testConfiggyConfiguration() {
    val model = """
    org.sidos.database.query.models
    {

      labelled
      {
        label : string
      }

      labelView
      {
        labelled : labelled
      }

      task : labelled
      {
         description : string      
      }

    }
    """
    println(System.getProperty("user.dir"));
    new Generator().generateFromSource(source,"org.sidos.model/src/generated")

//    SidosCompiler.compile(model).foreach(_type => println(_type.name + " " + _type.superTypes.size))

  }
}

