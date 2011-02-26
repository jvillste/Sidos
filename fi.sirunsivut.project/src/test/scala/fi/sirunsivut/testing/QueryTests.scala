package fi.sirunsivut.testing


import org.testng.annotations.Test
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.testng.TestNGSuite

import org.sidos.database.h2.H2ConnectionProvider
import org.sidos.database.DatabaseDefinition
import org.sidos.database.Database
import org.sidos.database.query.models._
import fi.sirunsivut.project.Task

class QueryTests extends TestNGSuite with ShouldMatchers {
    @Test
    def testQuery() {

      val database = new Database with H2ConnectionProvider with DatabaseDefinition
      database.createMetamodelSchema

      database.addType(Task.entityType)

      val task = Task.create(database)
      task.name.set("Do work")

      val query = Task.instances
      query.filters = List(Task.name.equals("Do work"))
      

      println("jees")
    }
}