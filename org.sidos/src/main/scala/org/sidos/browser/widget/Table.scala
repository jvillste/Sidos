package org.sidos.browser.widget


import java.util.UUID
import org.sidos.database.query.PropertyDefinition
import swing._
import org.sidos.database.Database
import org.sidos.database.notification._
import org.sidos.codegeneration.Property
import table.{ColumnModel, TableModel}

object TableConstructors
{
  def table(columnConstructors:(Database => ColumnModel)*)(database:Database) =
  {

    val tableModel = TableModel.create(database)

    columnConstructors.foreach(columnCreator => tableModel.columns.add(columnCreator(database)))

    tableModel

  }

  def column(property:Property)(database:Database) =
  {
    val columnModel = ColumnModel.create(database)
    columnModel.domainTypeHash.set(property.typeHash)
    columnModel.propertyName.set(property.propertyName)
    columnModel
  }

}

class Table(database:Database, val entities:ListDataSource[UUID], model : TableModel) extends BoxPanel(Orientation.Horizontal)
{

  model.columns.bind{
    _ match {
     case Add(index, value) => contents.insert(index, new Column(database,value,entities))
     case Remove(index) => contents.remove(index)
     case Change(index, value) => 
    }
  }

}

class Column(database:Database, model:ColumnModel, entities:ListDataSource[UUID]) extends BoxPanel(Orientation.Vertical)
{
  contents += new Label { text = model.propertyName.get }

  val propertyDefinition = new PropertyDefinition(model.domainTypeHash.get, model.propertyName.get)

  entities.bind{
    _ match {
     case Add(index, value) => contents.insert(index, new PropertyView(database,propertyDefinition, value))
     case Remove(index) => contents.remove(index)
     case Change(index, value) => 
    }
  }
}
