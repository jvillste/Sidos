package org.sidos.database.query

import org.sidos.database.Database
import java.util.UUID

class PropertyDefinition(val domainTypeHash:String, val propertyName:String)

class Sorting(propertyName:String, descending:Boolean)

trait FilterExpression
class BooleanExpression
{
  def or(otherBooleanExpression:BooleanExpression) = Or(this,otherBooleanExpression)
}
case class EqualsString(property:Property, value:String) extends BooleanExpression
case class Or(booleanExpression1:BooleanExpression,booleanExpression2:BooleanExpression) extends BooleanExpression

class Property
{
  def equalsString(value:String) = EqualsString(this,value)
}

object Person
{
  val name = new Property
}

class Tests
{

  Person.name.equalsString("FOO")  or Person.name.equalsString("FOO2")
}


class Filter(propertyName:String,filterOperator:FilterExpression )

class Query()
{
  var sortings = List.empty[Sorting]
  var filters = List.empty[Filter]
  
  def Count{}
  def ToList(take:Int, skip:Int) {}
  def First {}

}


class PropertyQuery(database:Database, subjectID:UUID, property:PropertyDefinition) extends Query

class InstanceQuery(typeHash:String) extends Query


