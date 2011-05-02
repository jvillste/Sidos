package org.sidos.codegeneration

import org.sidos.database.DataAccess

class SingleValueProperty[ModelType:ClassManifest,DatabaseType:ClassManifest](val entity:Entity,
                                                                val typeHash:String,
                                                                val propertyName:String,
                                                                databaseToModel : (DataAccess, DatabaseType) => ModelType,
                                                                modelToDatabase : (ModelType) => DatabaseType ) extends Property
{
  def get : ModelType = databaseToModel(entity.dataAccess, entity.get[DatabaseType](typeHash, propertyName))
  def set(value:ModelType) { entity.set(typeHash, propertyName,modelToDatabase(value)) }

  def addListener(callback : (ModelType)=>Unit)
  {
    entity.dataAccess.addListener(entity.id, propertyName){
      _ match {
        case value : DatabaseType => callback(databaseToModel(entity.dataAccess,value))
      }
    }
  }
}