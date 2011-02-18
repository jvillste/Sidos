package org.sidos.codegeneration

import org.sidos.database.Database
import java.util.{Date, UUID}

trait Entity{
  def database:Database
  def id:UUID

  def set[T:ClassManifest](typeHash:String, propertyName:String, value:T ) { database.set(typeHash,id,propertyName,value) }
  def get[T:ClassManifest](typeHash:String, propertyName:String) : T = database.get[T](typeHash,id,propertyName)
  def addToList[T:ClassManifest](typeHash:String, propertyName:String, value:T ) { database.addToList(typeHash, id, propertyName, value)}
  def getList[T:ClassManifest](typeHash:String, propertyName:String ) : List[T] = { database.getList[T](typeHash, id, propertyName) }
}
