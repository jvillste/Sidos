package org.sidos.codegeneration

import org.sidos.database.notification.{ListChange, Add, Remove, Change}

class ListProperty[T:ClassManifest](val entity:Entity, val typeHash:String, val propertyName:String) extends Property
{
  def add(value:T) =  entity.addToList(typeHash, propertyName, value)
  def get : List[T] = entity.getList[T](typeHash, propertyName)

  def addListener(callback : (ListChange[T])=>Unit)
  {
    entity.dataAccess.addListener(entity.id, propertyName){
      _ match {
        case Add(index, value : T) => callback(Add(index,value))
        case Remove(index) => callback(Remove(index))
        case Change(index, value : T) => callback(Change(index,value))
      }
    }
  }
}