package fi.sirunsivut.testing


import org.testng.annotations.Test
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.testng.TestNGSuite

import org.sidos.database.h2.H2ConnectionProvider
import org.sidos.database.DatabaseDefinition
import org.sidos.database.Database
import org.sidos.database.query.models._
import fi.sirunsivut.project.Task
import fi.sirunsivut.persons.Person

class QueryTests extends TestNGSuite with ShouldMatchers {
    @Test
    def testQuery() {

      val database = new Database with H2ConnectionProvider with DatabaseDefinition
      database.createMetamodelSchema

      database.addType(Task.entityType)
      database.addType(Person.entityType)

      val person = Person.create(database)
      person.name.set("Julle")

      val task = Task.create(database)
      task.name.set("Do work")
      task.responsibles.add(person)

      val query = Task.instances.where(Task.name.equalsString("Do work") or Task.responsibles.contains(person))

      println("jees")
    }
}