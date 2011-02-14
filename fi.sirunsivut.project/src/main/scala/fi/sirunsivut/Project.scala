package fi.sirunsivut

import org.sidos.model.{Type}
import org.sidos.model.compiler.{SidosCompiler}
import org.sidos.database.h2.H2ConnectionProvider
import persons.{Person, PersonRepository}
import project.{TaskRepository, Task}
import org.sidos.database.{DatabaseDefinition, Database}
import org.sidos.database.notification.{ListChange, Add}
import java.util.UUID
import org.sidos.browser.Browser
import org.sidos.metamodel.Type

object Project
{
  def main(args: Array[String])
  {

    val database = new Database with H2ConnectionProvider with DatabaseDefinition

    database.createMetamodelSchema
    

    database.addType(Person.entityType)


    val personRepository = new PersonRepository(database)
    val person = personRepository.create


    person.name.addListener{
      newValue =>
      println("changed to " + newValue)
    }

    person.name.set("Julle")
    println(person.name.get)



/*
    val browser1 = new Browser(database,person.id,person.typeHash,person.name.propertyName)
        browser1.pack
        browser1.visible = true

    val browser2 = new Browser(database,person.id,person.typeHash,person.name.propertyName)
        browser2.pack
        browser2.visible = true
*/
    val person2 = personRepository.getByName("Julle")
    println(person2.name.get)

    database.addType(Task.entityType)

    val taskRepository = new TaskRepository(database)
    val task = taskRepository.create

    task.responsibles.addListener{
      _ match {
        case Add(index,value) => println("Added responsible " + value.name.get)
      }
    }

    task.name.set("Do work")
    task.responsibles.add(person)

//    val query = Query.from(task).select(task => new { val name = task.name });

    println(task.responsibles.get(0).name.get)


    for(id <- database.getInstances(org.sidos.metamodel.Property.typeHash))
    {
      println(new org.sidos.metamodel.Property(database,id).name.get)
      println(new org.sidos.metamodel.Property(database,id).collectionType.get.id)
    }

    // Integration
/*
    val database2 = new Database with H2ConnectionProvider with DatabaseDefinition
    database2.integrateTo(database)


    val personRepository2 = new PersonRepository(database2)
    val person3 = personRepository.getByName("Julle")
    println(person3.name.get)

    val person4 = personRepository2.create
    person.name.set("Julle 2")
    database2.commit
*/

  }

}