package org.sidos.browser.widget

import org.sidos.database.Database
import swing._
import event._
import java.util.UUID
import org.sidos.database.query.PropertyDefinition

class PropertyView(database:Database, propertyDefinition:PropertyDefinition, subject:UUID) extends FlowPanel
{

  object field extends TextField {columns = 50}

  contents += field
  border = Swing.EmptyBorder(15, 10, 10, 10)

  field.text = database.get[String](propertyDefinition.domainTypeHash,subject,propertyDefinition.propertyName)
  database.addListener(subject,propertyDefinition.propertyName){
      _ match {
        case value : String => field.text = value
      }
  }

  listenTo(field)

  reactions += {
    case EditDone(field) =>
      database.set[String](propertyDefinition.domainTypeHash, subject,propertyDefinition.propertyName,field.text)
  }

}