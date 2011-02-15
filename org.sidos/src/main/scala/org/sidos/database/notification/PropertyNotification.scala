package org.sidos.database.notification

import scala.collection.mutable.{Map,Set => MutableSet}
import java.util.UUID
import org.sidos.database.DataAccess

case class ListChange[+T]()
case class Add[T](index:Int, value:T) extends ListChange[T]
case class Remove[T](index:Int) extends ListChange[T]
case class Change[T](index:Int, value:T) extends ListChange[T]

trait PropertyNotification extends DataAccess with Notifier
{

  abstract override def set[T:ClassManifest](typeHash: String, subject: UUID, propertyName: String, value: T) {
    super.set(typeHash,subject,propertyName,value)

    notifyChange(subject, propertyName, value)
  }

  abstract override def removeFromList(typeHash: String, subject: UUID, propertyName: String, index: Int)
  {
    super.removeFromList(typeHash, subject, propertyName, index)
    notifyChange(subject,propertyName,Remove(index))
  }

  abstract override def addToList[T:ClassManifest](typeHash: String, subject: UUID, propertyName: String, value: T) : Int =
  {
    val index = super.addToList[T](typeHash, subject, propertyName, value)

    notifyChange(subject,propertyName,Add(index, value))


    index
  }

  abstract override def insertIntoList[T:ClassManifest](typeHash: String, subject: UUID, propertyName: String, index: Int, value: T)
  {
    super.insertIntoList(typeHash, subject, propertyName, index, value)
    notifyChange(subject,propertyName,Add(index, value))

  }

}