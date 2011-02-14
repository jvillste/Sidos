package org.sidos.browser.common
import scala.collection.immutable.HashSet

trait ValueEditor[ValueType] extends ValueView[ValueType]{
	var valueEditListeners  =  HashSet.empty[(ValueType,ValueType) => Unit]
	
	protected def notifyEdit(oldValue : ValueType, newValue : ValueType){
		for(listener <- valueEditListeners)
			listener(oldValue,newValue)
	}
	
}