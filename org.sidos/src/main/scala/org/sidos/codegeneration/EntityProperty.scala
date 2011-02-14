package org.sidos.codegeneration

import java.util.UUID
import org.sidos.database.Database

class EntityProperty[T <: Entity](val entity:Entity, val propertyName:String, constructor : (Database, UUID)=>T) extends Property
{
  def get : T = constructor(entity.database, entity.get[UUID](propertyName))
  def set(value:T) : Unit = entity.set(propertyName,value.id)

  def addListener(callback : (T)=>Unit)
  {
    entity.database.addListener(entity.id, propertyName){
      _ match {
        case value : UUID => callback(constructor(entity.database,value))
      }
    }
  }
}