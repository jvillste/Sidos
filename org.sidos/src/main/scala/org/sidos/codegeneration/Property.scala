package org.sidos.codegeneration

trait Property
{
  def entity:Entity
  def propertyName:String

  def typeHash = entity.typeHash
}