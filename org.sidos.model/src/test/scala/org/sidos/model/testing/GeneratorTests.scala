
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
    //val list = List()
    //println(if(list.size > 0)  "extends " +  list.head + list.tail.foldLeft("")( _ + " with " + _) else "")
    //new org.sidos.codegeneration.Generator().generateFromSource(model,"org.sidos.model/src/generated")

//    SidosCompiler.compile(model).foreach(_type => println(_type.name + " " + _type.superTypes.size))

  }
}
