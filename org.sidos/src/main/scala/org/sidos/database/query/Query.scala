package org.sidos.database.query

import org.sidos.database.Database
import java.util.UUID
import org.sidos.model.Type
import org.sidos.codegeneration.Entity

class PropertyDefinition(val domainTypeHash:String, val propertyName:String)

case class Ordering(propertyName:String, descending:Boolean)

class BooleanExpression
{
  def or(otherBooleanExpression:BooleanExpression) = Or(this,otherBooleanExpression)
  def and(otherBooleanExpression:BooleanExpression) = And(this,otherBooleanExpression)

}

case class Like(propertyName:String, value:String) extends BooleanExpression
case class Or(booleanExpression1:BooleanExpression,booleanExpression2:BooleanExpression) extends BooleanExpression
case class And(booleanExpression1:BooleanExpression,booleanExpression2:BooleanExpression) extends BooleanExpression
case object True extends BooleanExpression

case class ContainsEntity(propertyName:String, id:UUID) extends BooleanExpression

trait QueryableProperty
{
  def domain:Type
  def name:String
  
  def ascending = Ordering(name,false)
  def descending = Ordering(name,true)
}

trait QueryableStringProperty extends QueryableProperty
{
  def like(value:String) = Like(name,value)
}

trait QueryableEntityListProperty[T <: Entity] extends QueryableProperty
{
  def contains(entity:T) = ContainsEntity(name, entity.id)
}

trait QueryableEntityProperty[T <: Entity] extends QueryableProperty
{
  def has(booleanExpression:BooleanExpression) = ContainsEntity(name, entity.id)
}


class Query()
{
  var orderings = List.empty[Ordering]
  var filter:BooleanExpression = True
  var takeCount:Option[Int] = None
  var skipCount:Option[Int] = None

  def where(booleanExpression:BooleanExpression) = { filter = booleanExpression; this }
  def orderBy(orderings:Ordering*) = { this.orderings = orderings.toList; this }
  def take(takeCount:Int) = { this.takeCount = Some(takeCount); this }
  def skip(skipCount:Int) = { this.skipCount = Some(skipCount); this }
}

case class InstanceQuery(typeHash:String) extends Query
