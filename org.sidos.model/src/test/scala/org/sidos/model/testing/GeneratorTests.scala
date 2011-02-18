
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

      filterOperator

      equalsString : filterOperator
      {
        value : string
      }
    }
    """
    SidosCompiler.compile(model).foreach(_type => println(_type.name + " " + _type.superTypes.size))

  }
}