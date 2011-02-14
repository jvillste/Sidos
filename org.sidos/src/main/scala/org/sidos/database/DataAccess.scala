package org.sidos.database

import java.util.UUID


trait DataAccess {

  def get[T:ClassManifest](subjectTypeHash:String, subject:UUID, propertyName:String) : T

  def getList[T:ClassManifest](subjectTypeHash:String, subject:UUID, propertyName:String) : List[T]

  def getInstances(subjectTypeHash:String) : List[UUID]

  def getBy[T:ClassManifest](subjectTypeHash:String, propertyName:String, value:T) : List[UUID]

  def insertIntoList[T:ClassManifest](typeHash: String, subject: UUID, propertyName: String, index:Int, value:T): Unit

  def addToList[T:ClassManifest](typeHash:String, subject:UUID, propertyName:String, value:T) : Int

  def removeFromList(typeHash: String, subject: UUID, propertyName: String, index:Int): Unit

  def set[T:ClassManifest](typeHash: String, subject: UUID, propertyName: String, value:T): Unit
}