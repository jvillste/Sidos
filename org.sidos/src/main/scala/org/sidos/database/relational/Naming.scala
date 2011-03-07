package org.sidos.database.relational

import org.sidos.model.{Property, Type}

trait Naming
{
  protected def getColumnName(propertyName:String) = propertyName.replace(".", "_")

  protected def getListTableName(property:Property) : String = getListTableName(property.domain.hash,property.name)
  protected def getListTableName(domainHash:String, propertyName:String) : String = "list_" + domainHash + "_" + propertyName.replace(".","_")

  protected def getTypeTableName(_type:Type) : String =  getTypeTableName(_type.hash)
  protected def getTypeTableName(hash:String) : String = "type_" + hash

}