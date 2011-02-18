package org.sidos.codegeneration

class SingleValueProperty[T:ClassManifest](val entity:Entity, val typeHash:String, val propertyName:String) extends Property
{
  def get : T = entity.get[T](typeHash, propertyName)
  def set(value:T) { entity.set(typeHash, propertyName,value) }

  def addListener(callback : (T)=>Unit)
  {
    entity.database.addListener(entity.id, propertyName){
      _ match {
        case value : T => callback(value)
      }
    }
  }
}