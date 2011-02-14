package org.sidos.database.query

import org.sidos.database.Database
import java.util.UUID

class PropertyDefinition(val domainTypeHash:String, val propertyName:String)

class Sorting(propertyName:String, descending:Boolean)

class FilterOperator
class Equals[T] extends FilterOperator
class GreaterThan[T] extends FilterOperator
class LessThan[T] extends FilterOperator
class HasValue extends FilterOperator

class Filter(propertyName:String,filterOperator:FilterOperator )

class Query()
{
  var sortings = List.empty[Sorting]
  var filters = List.empty[Filter]
  
  def Count{}
  def ToList(take:Int, skip:Int) {}
  def First {}
}


class PropertyQuery(database:Database, subjectID:UUID, property:PropertyDefinition) extends Query

class InstanceQuery(database:Database, typeHash:String)
