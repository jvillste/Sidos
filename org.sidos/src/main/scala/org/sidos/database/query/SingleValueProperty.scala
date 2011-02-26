package org.sidos.database.query

import models._
import org.sidos.database.Database

trait SingleValueProperty[T] {
  def propertyName:String
  def typeHash:String


  def orderBy = (database:Database) => new SortingEntity(database, database.createEntity(Sorting.typeHash))

  def equals(value:T) =
  {
    value match {
      case v : String => {(database:Database) =>

        val equalsString = EqualsString.create(database)
        equalsString.value.set(value)

        val filter = Filter.create(database)
        filter.filterOperator.set(equalsString)
        filter.propertyName.set(propertyName)
        filter.subjectTypeHash.set(typeHash)
        filter

      }
    }
  }

}