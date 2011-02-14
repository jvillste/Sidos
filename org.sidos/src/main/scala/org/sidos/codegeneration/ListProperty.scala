package org.sidos.codegeneration

import org.sidos.database.notification.{ListChange, Add, Remove, Change}

class ListProperty[T:ClassManifest](val entity:Entity, val propertyName:String) extends Property
{
  def add(value:T) =  entity.addToList(propertyName, value)
  def get : List[T] = entity.getList[T](propertyName)

  def addListener(callback : (ListChange[T])=>Unit)
  {
    entity.database.addListener(entity.id, propertyName){
      _ match {
        case Add(index, value : T) => callback(Add(index,value))
        case Remove(index) => callback(Remove(index))
        case Change(index, value : T) => callback(Change(index,value))
      }
    }
  }
}