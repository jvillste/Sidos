package org.sidos.browser.common
import scala.swing.Component
import org.sidos.browser.widget.ViewDefinition


trait IViewFactory {
	def CreateDefaultValueView[ValueType](value : ValueType) : ValueView[ValueType]
	def CreateDefaultValueEditor[ValueType](value : ValueType) : ValueEditor[ValueType]

  def CreateView(viewDefinition : ViewDefinition) : Component
}