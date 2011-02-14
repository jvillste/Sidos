package org.sidos.database.notification

trait SingleValueDataSource[T]
{
  def bind(callback : (T) => Unit)
}






