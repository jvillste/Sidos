package org.sidos.browser.widget

import scala.swing.TextField
import scala.swing.event.{KeyPressed,Key}
import org.sidos.browser.common.ValueEditor

class TextEditor(var value: String = "") extends TextField with ValueEditor[String]{
	text = value
	
	listenTo(keys)
	reactions += {
		case KeyPressed(panel, Key.Enter, modifiers, location) =>
			if(value != text)
			{
				notifyEdit(value,text)
				value = text
			}
	}

}