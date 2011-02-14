package org.sidos.database

import juvi.JDBC
import org.sidos.model.{Type,Property,AssociationType}
import java.util.{Date, UUID}
import java.sql.{Timestamp, PreparedStatement, ResultSet}

trait Relational extends DataAccess with JDBC
{

  def createTypeTables(_type: Type): Unit = {
    val (listProperties, nonListProperties) = _type.properties.partition(_.associationType == AssociationType.List)

    val columnDefinitions = ("id uuid PRIMARY KEY" :: nonListProperties.map(getColumnDefinition(_))).mkString(", ")

    executeStatement("create table " + getTypeTableName(_type) + "(" + columnDefinitions + ")")

    for (property <- listProperties) {
      executeStatement("create table " + getListTableName(property) + "(subject uuid, value " + getSQLType(property.range) + ", index int)")
    }
  }


  private case class Primitive(name:String, SQLType:String)
  {
    def fullName = "org.sidos.primitive." + name
  }

  private val primitives = List(Primitive("string", "varchar(500)"),
                        Primitive("integer", "int"),
                        Primitive("time", "timestamp"),
                        Primitive("boolean", "boolean"))

  private val primitiveTypeNames = primitives.map(_.fullName).toSet

  private val SQLTypes = primitives.map((primitiveType) => (primitiveType.fullName -> primitiveType.SQLType)).toMap

  private def getSQLType(_type:Type) = SQLTypes.getOrElse(_type.name, "uuid")

  private def getColumnDefinition(property:Property) =
  {
    if(property.associationType != AssociationType.List)
    {
      getColumnName(property.name) + " " + getSQLType(property.range)
    }else throw new Exception()
  }

  private def getColumnName(propertyName:String) = propertyName.replace(".", "_")

  private def getListTableName(property:Property) : String = getListTableName(property.domain.hash,property.name)
  private def getListTableName(domainHash:String, propertyName:String) : String = "list_" + domainHash + "_" + propertyName.replace(".","_")

  private def getTypeTableName(_type:Type) : String =  getTypeTableName(_type.hash)
  private def getTypeTableName(hash:String) : String = "type_" + hash

  private val stringClass = classOf[String]
  private val intClass = classOf[Int]
  private val dateClass = classOf[Date]
  private val uuidClass = classOf[UUID]

  private def getValue[T](resultSet:ResultSet)(implicit classManifest : ClassManifest[T]) =
  {
    (classManifest.erasure match
      {
        case `stringClass` => resultSet.getString(1)
        case `intClass` => resultSet.getInt(1)
        case `dateClass` => new Date(resultSet.getTimestamp(1).getTime)
        case `uuidClass` => resultSet.getObject(1)
        case _ => throw new Exception("Invalid value type " + classManifest.erasure)
      }).asInstanceOf[T]
  }

  private def setValue[T](preparedStatement:PreparedStatement, value:T)(implicit classManifest : ClassManifest[T])
  {
    
    (classManifest.erasure match
      {
        case `stringClass` => preparedStatement.setString(1,value.asInstanceOf[String])
        case `intClass` => preparedStatement.setInt(1,value.asInstanceOf[Int])
        case `dateClass` => preparedStatement.setTimestamp(1,new Timestamp(value.asInstanceOf[Date].getTime))
        case `uuidClass` => preparedStatement.setObject(1,value)
        case _ => throw new Exception("Invalid value type " + classManifest.erasure)
      }).asInstanceOf[T]
  }

  def get[T:ClassManifest](subjectTypeHash:String, subject:UUID, propertyName:String) : T =
  {

     usingPreparedStatement("SELECT " + getColumnName(propertyName) + " from " + getTypeTableName(subjectTypeHash) + " where id = ?"){ statement =>

        statement.setObject(1, subject)

        val resultSet = statement.executeQuery()
        if (resultSet.next())
          return getValue(resultSet)
        else
          return null.asInstanceOf[T]
      }
  }

  def getList[T:ClassManifest](subjectTypeHash:String, subject:UUID, propertyName:String) : List[T] =
  {
      var results = List.empty[T]

      usingPreparedStatement("SELECT value from " + getListTableName(subjectTypeHash, propertyName) + " where subject = ? order by index asc"){ statement =>

        statement.setObject(1, subject)

        val resultSet = statement.executeQuery()

        while(resultSet.next()) {
          results = getValue(resultSet) :: results
        }
    }

    results
  }

  def getInstances(subjectTypeHash:String) : List[UUID] =
  {

     var results = List.empty[UUID]

     usingPreparedStatement("SELECT id from " + getTypeTableName(subjectTypeHash))
     { statement =>

        val resultSet = statement.executeQuery()

        while(resultSet.next()) {
          results = resultSet.getObject(1).asInstanceOf[UUID] :: results
        }
     }

     results

  }

  def getBy[T:ClassManifest](subjectTypeHash:String, propertyName:String, value:T) : List[UUID] =
  {

     var results = List.empty[UUID]

     usingPreparedStatement("SELECT id from " + getTypeTableName(subjectTypeHash) +
                            " where " + getColumnName(propertyName) + " = ?")
     { statement =>

        setValue(statement,value)

        val resultSet = statement.executeQuery()

        while(resultSet.next()) {
          results = resultSet.getObject(1).asInstanceOf[UUID] :: results
        }
     }

     results

  }


  def insertIntoList[T:ClassManifest](typeHash: String, subject: UUID, propertyName: String, index:Int, value:T): Unit = {
    val listTableName = getListTableName(typeHash, propertyName)

    usingPreparedStatement("update " + listTableName + " set index = ( index + 1 ) where subject = ? and index > ?") {
      statement =>
        statement.setObject(1,subject)
        statement.setInt(2,index)
        statement.execute
    }

    usingPreparedStatement("insert into " + listTableName + " (value, subject, index) values (?,?,?)") {
        statement =>
          setValue(statement,value)
          statement.setObject(2,subject)
          statement.setInt(3,index)

          statement.execute
    }
  }


  def addToList[T:ClassManifest](typeHash:String, subject:UUID, propertyName:String, value:T) : Int = {
    val listTableName = getListTableName(typeHash, propertyName)

    var index = 0

    usingPreparedStatement("SELECT max(index) from " + listTableName + " where subject = ?") { statement =>
      statement.setObject(1, subject)
      val resultSet = statement.executeQuery()
      resultSet.next()
      val index = resultSet.getInt(1)

      insertIntoList(typeHash, subject, propertyName, index,value)
    }

    index
  }

  def removeFromList(typeHash: String, subject: UUID, propertyName: String, index:Int): Unit = {
      val listTableName = getListTableName(typeHash, propertyName)

      usingPreparedStatement("delete from " + listTableName + " where id = ? and index = ?") {
            statement =>
              statement.setObject(1,subject)
              statement.setInt(2,index)
              statement.execute
      }

      usingPreparedStatement("update " + listTableName + " set index = ( index - 1 ) where id = ? and index > ?") {
        statement =>
          statement.setObject(1,subject)
          statement.setInt(2,index)
          statement.execute
      }

    }


    def set[T:ClassManifest](typeHash: String, subject: UUID, propertyName: String, value:T): Unit = {

      val typeTableName = getTypeTableName(typeHash)
      usingPreparedStatement("SELECT count(*) from " + typeTableName + " where id = ?") {
        statement =>
          statement.setObject(1, subject)
          val resultSet = statement.executeQuery()
          resultSet.next()

          if (resultSet.getInt(1) == 0) {
            usingPreparedStatement("insert into " + typeTableName + " (" + getColumnName(propertyName) + ", id) values (?,?)") {
              statement =>
                statement.setObject(2,subject)
                setValue(statement, value)
                statement.execute

            }
          } else {
            usingPreparedStatement("update " + typeTableName + " set " + getColumnName(propertyName) + " = ? where id = ?") {
              statement =>
                setValue(statement, value)
                statement.setObject(2, subject)
                statement.execute
            }
          }
      }
    }

}