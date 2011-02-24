/*
 * Created by IntelliJ IDEA.
 * User: Jukka
 * Date: 19.2.2011
 * Time: 0:47
 */
package org.sidos.tests

import org.testng.annotations.Test
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.testng.TestNGSuite

import org.sidos.database.h2.H2ConnectionProvider
import org.sidos.database.DatabaseDefinition
import org.sidos.database.Database
import org.sidos.database.query.models.{EqualsString, Query, EqualsStringRepository, QueryRepository}

class QueryTests extends TestNGSuite with ShouldMatchers {
    @Test
    def testQuery() {

      val database = new Database with H2ConnectionProvider with DatabaseDefinition
      database.createMetamodelSchema
      database.addType(Query.entityType)
      database.addType(EqualsString.entityType)

      val query = new QueryRepository(database).create
      val equalsString = new EqualsStringRepository(database).create
      equalsString.value.set("foo")
      query.filters.get.head.filterOperator.set(equalsString)
      println("jees")
    }
}