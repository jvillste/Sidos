package org.sidos.tests

import org.scalatest.Suite

import org.sidos.model.compiler.SidosCompiler
import org.sidos.model.Type
import org.sidos.database.{DatabaseDefinition, Database}
import org.sidos.database.h2.H2ConnectionProvider
import org.h2.tools.Server
import java.util.UUID
import org.sidos.codegeneration.Generator

class GeneratorTests  extends Suite {

  def testParser
  {

    def schema =
      """

      fi.sirunsivut.persons {

        person {
          name : string
          nickNames : string list
        }
      }

      fi.sirunsivut.project {
        fi.sirunsivut.persons as persons

        task {
          name : string
          responsibles : persons.person list
          created : time
        }

        call : task {
          numberToCall : string
        }

      }

    """

    val types = SidosCompiler.compile(schema)



    object database extends Database with H2ConnectionProvider with DatabaseDefinition

    database.createMetamodelSchema

    for(_type <- types)
    {
      database.addType(_type)
    }

    val personType = types.find(_.name.equals("fi.sirunsivut.persons.person")).get
    new Generator().generate(personType,null)

    /*
    val server = Server.createWebServer().start();
    println("Started: " + server.getURL)
    System.in.read
    server.stop
*/

    /*
    val personRepository = new PersonRepository(database)

    val personType = types.find(_.name.equals("fi.sirunsivut.persons.person")).get
    println(personType.hash)
    
    val person = personRepository.create
    person.name.set("Julle2")
    println(person.name.get)

    person.nickNames.add("jukkis")
    person.nickNames.add("jukkis2")
    println(person.nickNames.get(0))
    println(person.nickNames.get(1))
    
    val person2 = personRepository.getByName("Julle2")
    person2.name.set("Julle3")
    println(person.name.get)
*/
    /*
    val personType = types.find(_.name.equals("fi.sirunsivut.persons.person")).get
    println("person hash " + personType.hash)
    val personEntity = database.createEntity(personType.hash)
    println("setting name")
    database.setString(personType.hash,personEntity,"fi.sirunsivut.persons.person.name","Julle")
    println(database.getString(personType.hash,personEntity,"fi.sirunsivut.persons.person.name"))
*/

  }

}