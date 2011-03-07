package org.sidos.codegeneration

import java.util.{Date, UUID}
import org.sidos.database.{DataAccess, Database}

trait Entity{
  def dataAccess:DataAccess
  def id:UUID

  def set[T:ClassManifest](typeHash:String, propertyName:String, value:T ) { dataAccess.set(typeHash,id,propertyName,value) }
  def get[T:ClassManifest](typeHash:String, propertyName:String) : T = dataAccess.get[T](typeHash,id,propertyName)
  def addToList[T:ClassManifest](typeHash:String, propertyName:String, value:T ) { dataAccess.addToList(typeHash, id, propertyName, value)}
  def getList[T:ClassManifest](typeHash:String, propertyName:String ) : List[T] = { dataAccess.getList[T](typeHash, id, propertyName) }
}
