package org.sidos.database.relational

import org.sidos.database.query.InstanceQuery
import java.util.UUID
import juvi.JDBC
import org.sidos.database.DataAccess


trait RelationalQueryRunner extends JDBC with Naming with DataAccess {

  def list[PatternType,EntityType](instanceQuery:InstanceQuery[PatternType,EntityType]) : List[EntityType] =
  {

    var results = List.empty[EntityType]

    usingPreparedStatement("SELECT id from " + getTypeTableName(instanceQuery.typeHash))
     { statement =>

        val resultSet = statement.executeQuery()

        while(resultSet.next()) {
          results = instanceQuery.enitityFactory(this, resultSet.getObject(1).asInstanceOf[UUID]) :: results
        }
     }

     results
  }
}