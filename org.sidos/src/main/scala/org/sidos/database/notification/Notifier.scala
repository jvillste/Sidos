package org.sidos.database.notification

import java.util.UUID
import collection.mutable.{HashMap, MultiMap, Set => MutableSet}



trait Notifier
{
  type Listener = (Any) => Unit

  private def getKey(subject:UUID, propertyName:String) = subject.toString + propertyName

  private val listeners = new HashMap[String, MutableSet[Listener]] with MultiMap[String,Listener]

  def addListener(subject: UUID, propertyName:String)(callback:Listener)
  {
    listeners.addBinding(getKey(subject,propertyName),callback)
  }

  def removeListener(subject: UUID, propertyName:String, callback:Listener)
  {
    listeners.removeBinding(getKey(subject,propertyName),callback)
  }

  def notifyChange(subject:UUID, propertyName:String, parameter:Any)
  {
    listeners.getOrElse(getKey(subject, propertyName),
                                    MutableSet.empty).foreach(_.apply(parameter))

  }
}
