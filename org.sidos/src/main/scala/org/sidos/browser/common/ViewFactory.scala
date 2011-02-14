package org.sidos.browser.common
import scala.swing.Component


trait ViewFactory {
	def CreateValueView[ValueType](value : ValueType) : ValueView[ValueType]
	def CreateValueEditor[ValueType](value : ValueType) : ValueEditor[ValueType]
}