package org.sidos.browser.common
/*
import org.sidos.browser.widget.{TextEditor, Outline}

class BuildInViewFactory extends ViewFactory {
	override def CreateValueView[ValueType](value : ValueType) : ValueView[ValueType] = {
		if(value.isInstanceOf[String]) new Outline(value)
		else null
	}

	def CreateValueEditor[ValueType](value : ValueType) : ValueEditor[ValueType] = {
		if(value.isInstanceOf[String]) (new TextEditor(value.asInstanceOf[String])).asInstanceOf[ValueEditor[ValueType]]
		else null
		
	}
}
*/