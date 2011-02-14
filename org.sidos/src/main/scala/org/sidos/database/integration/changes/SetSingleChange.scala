package org.sidos.database.integration.changes

import java.util.UUID

case class SetSingleChange(typeHash:String, subject:UUID, propertyName:String, value:String) extends Change