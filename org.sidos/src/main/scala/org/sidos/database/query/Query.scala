package org.sidos.database.query

import java.util.UUID
import org.sidos.model.Type
import org.sidos.codegeneration.Entity
import org.sidos.database.{DataAccess, Database}

class PropertyDefinition(val domainTypeHash:String, val propertyName:String)

case class Ordering(path:List[String], descending:Boolean)

class BooleanExpression
{
  def or(otherBooleanExpression:BooleanExpression) = Or(this,otherBooleanExpression)
  def and(otherBooleanExpression:BooleanExpression) = And(this,otherBooleanExpression)
}

case class Like(path:List[String], value:String) extends BooleanExpression
case class Or(booleanExpression1:BooleanExpression,booleanExpression2:BooleanExpression) extends BooleanExpression
case class And(booleanExpression1:BooleanExpression,booleanExpression2:BooleanExpression) extends BooleanExpression
case object True extends BooleanExpression

case class ContainsEntity(path:List[String], id:UUID) extends BooleanExpression
case class Equals[T](path:List[String], value:T) extends BooleanExpression

trait QueryableProperty
{
  def path:List[String]

  def _ascending = Ordering(path, false)
  def _descending = Ordering(path, true)
}

trait QueryableStringProperty extends QueryableProperty
{
  def _like(value:String) = Like(path, value)
}

trait QueryableBooleanProperty extends QueryableProperty
{
  def _equals(value:Boolean) = Equals(path, value)
}

trait QueryableTimeProperty extends QueryableProperty
{
  def _equals(value:java.util.Date) = Equals(path, value)
}


trait QueryableEntityListProperty[T <: Entity] extends QueryableProperty
{
  def _contains(entity:T) = ContainsEntity(path, entity.id)
}

trait QueryableEntityProperty[T <: Entity] extends QueryableProperty
{
  def _equals(entity:T) = Equals(path, entity.id)
}

// Join("client",Join("colleague", Like("name","Jukka")))

class Query[PatternType](patternFactory : () => PatternType)
{
  var orderings = List.empty[Ordering]
  var filter:BooleanExpression = True
  var takeCount:Option[Int] = None
  var skipCount:Option[Int] = None

  def where(booleanExpressionGenerator:(PatternType)=>BooleanExpression) = { filter = booleanExpressionGenerator(patternFactory()); this }
  def orderBy(orderingGenerator:(PatternType)=>List[Ordering]) = { this.orderings = orderingGenerator(patternFactory()); this }
  def take(takeCount:Int) = { this.takeCount = Some(takeCount); this }
  def skip(skipCount:Int) = { this.skipCount = Some(skipCount); this }
}

case class InstanceQuery[PatternType,EntityType](typeHash:String,  patternFactory : () => PatternType, val enitityFactory:(DataAccess,UUID) => EntityType) extends Query[PatternType](patternFactory)

