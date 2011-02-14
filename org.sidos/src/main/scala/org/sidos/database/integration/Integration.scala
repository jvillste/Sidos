package org.sidos.database.integration

import changes.Change
import java.util.UUID
import org.sidos.database.Database

abstract class Integration(val remoteDatabase:Database)
{
  class LoadedProperty(subjectTypeHash:String, subject:UUID, propertyName:String)

  val changes : List[Change]
  val loadedProperties : Set[LoadedProperty]
}
