package tk.pratanumandal.roboto;

import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.JPanel;

public class JPanel2 extends JPanel {
	
	private static final long serialVersionUID = -1297452901595958270L;
	
	private JPanel outerPanel;
	
	public JPanel2(JPanel outerPanel) {
		super();
		this.outerPanel = outerPanel;
	}
	
	public Dimension getPreferredSize() {
		Insets insets = outerPanel.getInsets();
		Insets borderInsets = outerPanel.getBorder().getBorderInsets(this);
		System.out.println(outerPanel.getVisibleRect());
		System.out.println(insets);
		System.out.println(outerPanel.getWidth() - (insets.left + insets.right));
		return new Dimension(outerPanel.getWidth() - (insets.left + insets.right), (int) super.getPreferredSize().getHeight());
	}
}
