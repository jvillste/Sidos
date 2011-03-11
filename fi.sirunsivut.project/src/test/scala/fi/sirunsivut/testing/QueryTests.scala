package fi.sirunsivut.testing


import org.testng.annotations.Test
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.testng.TestNGSuite

import org.sidos.database.h2.H2ConnectionProvider
import org.sidos.database.DatabaseDefinition
import org.sidos.database.Database
import org.sidos.database.query.models._
import fi.sirunsivut.persons.Person
import org.sidos.database.relational.RelationalQueryRunner
import fi.sirunsivut.project.{TaskEntity, Task}

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

      val task2 = Task.create(database)
      task2.name.set("Don't do work")

      /*
      val query = Task.instances.where(task => task.name._like("Do%") or task.responsibles._contains(person)).orderBy(task => List(task.name._ascending)).skip(10).take(10)
      println(query.filter)
      val query2 = Task.instances.where(task => task.client.colleague.name._like("Julle"))
      println(query2.filter)
*/
      database.list(Task.instances.where(task => task.name._like("Do%"))).foreach((task:TaskEntity) => println(task.name.get))

    }
}