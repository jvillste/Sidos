package org.sidos.tests

import org.scalatest.Suite
import org.sidos.database.h2.H2ConnectionProvider
import org.sidos.database.{DatabaseDefinition, Database}
import reflect.ClassManifest
import java.sql.ResultSet

class DatabaseTests extends Suite {
  def testDatabase{
    object database extends Database with H2ConnectionProvider with DatabaseDefinition
    {
      override def databasePath= "db2"
    }
    
  }

  def testTypes{
    def get[T](implicit manifest : ClassManifest[T]) : T = {

      val stringClass = classOf[String]
      val intClass = classOf[Int]

      (manifest.erasure match
      {
        case `stringClass` => "Jees"
        case `intClass` => 10
        case _ => null
      }).asInstanceOf[T]
    }

    val defaults = List((classOf[String] -> ""),
                            (classOf[Int] -> 0)).toMap


    println(defaults(classOf[String]))

    println("testing type 2s")
    println(get[String])
    println(get[Int])
  }
}