package org.sidos.database.relational

import java.util.UUID
import juvi.JDBC
import org.sidos.database.DataAccess
import org.sidos.database.query.{Query, InstanceQuery, Like}


trait RelationalQueryRunner extends JDBC with Naming with DataAccess {

  def list[PatternType,EntityType](query:Query[PatternType]) : List[EntityType] =
  {
    var results = List.empty[EntityType]

    query match
    {
      case instanceQuery:InstanceQuery[PatternType, EntityType] =>

      var where = " WHERE "

      instanceQuery.filter match {
        case Like(path, value) => where += getColumnName(path.head) + " LIKE '" + value + "'"
      }

      val statement = "SELECT id FROM " + getTypeTableName(instanceQuery.typeHash) + where
      println(statement)
      usingPreparedStatement(statement)
       { statement =>

          val resultSet = statement.executeQuery()

          while(resultSet.next()) {
            results = instanceQuery.enitityFactory(this, resultSet.getObject(1).asInstanceOf[UUID]) :: results
          }
       }
    }
     results
  }
}