package org.sidos.browser.common
import scala.swing.Component
import javax.swing.JComponent
import java.awt.Dimension

class SizeGroupablePanel extends Component {
	override lazy val peer: javax.swing.JPanel = new javax.swing.JPanel with SuperMixin with SizeGroupableComponent
	
	protected trait SizeGroupableComponent extends JComponent
	{
		override def getMinimumSize() = {
			new Dimension(10,10)
		}
	}
}
