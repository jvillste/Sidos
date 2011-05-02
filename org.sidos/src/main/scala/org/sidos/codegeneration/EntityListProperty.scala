package org.sidos.codegeneration

import java.util.UUID
import org.sidos.database.notification._
import org.sidos.database.{DataAccess, Database}

class EntityListProperty[T <: Entity](val entity:Entity, val typeHash:String, val propertyName:String, constructor : (DataAccess, UUID)=>T) extends ListDataSource[T] with Property
{
  def add(value:T) : Unit =  entity.addToList(typeHash, propertyName, value.id)
  def get : List[T] = entity.getList[UUID](typeHash, propertyName).map(constructor(entity.dataAccess,_))

  def bind(callback : (ListChange[T])=>Unit)
  {
    val values : List[T] = get

    (0 to values.size - 1).foreach { index =>
      callback(Add(index,values(index)))
    }

    addListener(callback)
  }

  def addListener(callback : (ListChange[T])=>Unit)
  {
    entity.dataAccess.addListener(entity.id, propertyName){
      _ match {
        case Add(index, value : UUID) => callback(Add(index,constructor(entity.dataAccess,value)))
        case Remove(index) => callback(Remove(index))
        case Change(index, value : UUID) => callback(Change(index,constructor(entity.dataAccess,value)))
      }
    }
  }
}
