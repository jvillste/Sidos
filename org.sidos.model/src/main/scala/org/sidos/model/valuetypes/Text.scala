package org.sidos.model.valuetypes

class Text(val value : String) extends ValueType

object Text
{
  implicit def textToString(text:Text) : String  = text.value
  implicit def stringToText(string:String) : Text  = new Text(string)
}