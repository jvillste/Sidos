package org.sidos.codegeneration

import org.sidos.database.notification.{ListChange, Add, Remove, Change}
import org.sidos.database.DataAccess

class ListProperty[ModelType:ClassManifest, DatabaseType:ClassManifest](val entity:Entity,
                                                          val typeHash:String,
                                                          val propertyName:String,
                                                          databaseToModel : (DataAccess, DatabaseType) => ModelType,
                                                          modelToDatabase : (ModelType) => DatabaseType) extends Property
{
  def add(value:ModelType) =  entity.addToList(typeHash, propertyName, modelToDatabase(value))
  def add(traversable:Traversable[ModelType]) = for(value <- traversable) entity.addToList(typeHash, propertyName, modelToDatabase(value))
  def get : List[ModelType] = entity.getList[DatabaseType](typeHash, propertyName).map(databaseToModel(entity.dataAccess,_))

  def bind(callback : (ListChange[ModelType])=>Unit)
  {
    val values = get

    (0 to values.size - 1).foreach { index =>
      callback(Add(index,values(index)))
    }

    addListener(callback)
  }

  def addListener(callback : (ListChange[ModelType])=>Unit)
  {
    entity.dataAccess.addListener(entity.id, propertyName){
      _ match {
        case Add(index, value : DatabaseType) => callback(Add(index,databaseToModel(entity.dataAccess, value)))
        case Remove(index) => callback(Remove(index))
        case Change(index, value : DatabaseType) => callback(Change(index,databaseToModel(entity.dataAccess, value)))
      }
    }
  }
}