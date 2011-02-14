package org.sidos.browser.widget

import scala.swing.{ BoxPanel, Orientation,Component }
import org.sidos.browser.common.ValueView

/*
class Reference
class ViewDefinition
trait Database
class Property


trait ViewFactory{
	def createView(viewDefinition:ViewDefinition) : Component
}

trait ListDataSourceListener[T]
{
	def add(index:Int,element:T)
	def remove(index:Int)
}

abstract class ListDataSource[T] 
{
	def bind(listener:ListDataSourceListener[T])
}

class BrowserContext(val viewFactory:ViewFactory,val database:Database)

class PropertyDefinition(val property:Property, val viewDefinition:ViewDefinition)

case class OutlineViewDefinition(valueViewDefinition: ViewDefinition,
								 properties : ListDataSource[PropertyDefinition] = null)

class Outline[ValueType](var value: ValueType,
						viewDefinition:OutlineViewDefinition,
						browserContext:BrowserContext) extends BoxPanel(Orientation.Horizontal) with ValueView[ValueType] {

  viewDefinition.properties.bind(new ListDataSourceListener[PropertyDefinition]
	{
		def add(index:Int,propertyDefinition:PropertyDefinition )
		{
		  contents.insert(index,browserContext.viewFactory.createView(propertyDefinition.viewDefinition))
		}	
	
		def remove(index:Int)
		{
			contents.remove(index)
		}
	})
  
}
*/
