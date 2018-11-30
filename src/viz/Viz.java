package viz;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;
import RdmGridToNet.*;


public class Viz extends JFrame {

	/** The reaction-diffusion layer computed elsewhere. */
	protected layerRd lRd ;

	/** Image of one of the morphogens. */
	protected BufferedImage iRd;

	/** Pencils box. */
	protected Graphics2D gfx; 

	/** Where to draw. */
	protected JPanel canvas;

	public Viz(layerRd lrd) {
		int[] size = lrd.getSizeGrid();

		lRd = lrd;
//		iRd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(size, size);
		iRd = new BufferedImage(size[0], size[1], BufferedImage.TYPE_INT_ARGB);
		gfx = iRd.createGraphics();

		canvas = new RDPanel(iRd);

		setLayout(new BorderLayout());
		add(canvas, BorderLayout.CENTER);
		pack();
		setSize(new Dimension(getInsets().right + getInsets().left + size[0], getInsets().top + getInsets().bottom + size[1]));
		setVisible(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void step() {
		renderImage();
		drawImage();
	}

	protected void renderImage() { 
		int size = iRd.getWidth();

		assert(lRd.getSizeGrid()[0] == size && lRd.getSizeGrid()[1] == size);

		for(int y=0; y<size; y++) {
			for(int x=0; x<size; x++) {
				iRd.setRGB(x, y, Color.HSBtoRGB((float)(lRd.getCell(x, y).getVal1()/1.0), 1f, 1f));
			}
		}
	}

	protected void drawImage() {
		canvas.repaint();
	}

	class RDPanel extends JPanel {
		protected BufferedImage iRd;
		
		public RDPanel(BufferedImage ird) {
			iRd = ird;
			setPreferredSize(new Dimension(iRd.getWidth(), iRd.getHeight()));
		}

		public void paintComponent(Graphics g) {
			g.drawImage(iRd, 0, 0, this);
		}
	}
}