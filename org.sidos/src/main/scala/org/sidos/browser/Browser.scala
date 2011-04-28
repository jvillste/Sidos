package org.sidos.browser

import swing._
import org.sidos.database.Database
import java.util.UUID
import org.sidos.database.query.PropertyDefinition
import widget._
import org.sidos.database.notification.ListDataSource
import org.sidos.codegeneration.Entity

class Browser[T](database:Database, viewDefinition:Entity) extends MainFrame
{


  title = "Sidos Browser"

  contents = new FlowPanel {
    //contents += new widget.Table(database,entities, table(column(typeHash,propertyName)))
    border = Swing.EmptyBorder(15, 10, 10, 10)
  }
}