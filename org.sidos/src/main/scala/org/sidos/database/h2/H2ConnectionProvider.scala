package org.sidos.database.h2

import org.h2.jdbcx.JdbcConnectionPool
import org.sidos.database.DatabaseDefinition
import juvi.ConnectionProvider

trait H2ConnectionProvider extends ConnectionProvider
{
  this: DatabaseDefinition =>

  Class.forName("org.h2.Driver");
  private val connectionPool = JdbcConnectionPool.create(connectionString, userName, password);

  private def connectionString =
  {
    "jdbc:h2:" + (if(inMemory) "mem:" else "") + databasePath
  }

  def getConnection =
  {
    connectionPool.getConnection

  }
}
