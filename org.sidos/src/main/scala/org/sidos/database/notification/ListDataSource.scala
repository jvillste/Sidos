package org.sidos.database.notification

trait ListDataSource[T]
{
  def bind(callback : (ListChange[T])=>Unit)
}








