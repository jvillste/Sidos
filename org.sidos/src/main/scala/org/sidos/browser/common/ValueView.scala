package org.sidos.browser.common
import scala.swing.Component

trait ValueView[ValueType] extends Component{
	var value : ValueType
}