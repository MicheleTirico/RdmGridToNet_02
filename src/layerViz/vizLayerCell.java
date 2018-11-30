package layerViz;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

import RdmGridToNetMultiLayer.layerCell;
import RdmGridToNetMultiLayer.layerStaticCell;

public class vizLayerCell extends JFrame {
	
	protected int[] sizeGrid ;
	protected int posVal ;
	/** The reaction-diffusion layer computed elsewhere. */
	protected layerCell lC ;

	/** Image of one of the morphogens. */
	protected BufferedImage iRd;

	/** Pencils box. */
	protected Graphics2D gfx; 

	/** Where to draw. */
	protected JPanel canvas;

	public vizLayerCell(layerCell lC , int posVal ) {
		this.sizeGrid = lC.getSizeGrid();
		this.lC = lC;
		this.posVal = posVal ;

		iRd = new BufferedImage(sizeGrid[0], sizeGrid[1], BufferedImage.TYPE_INT_ARGB);
		gfx = iRd.createGraphics();

		canvas = new RDPanel(iRd);

		setLayout(new BorderLayout());
		add(canvas, BorderLayout.CENTER);
		pack();
		setSize(new Dimension(getInsets().right + getInsets().left + sizeGrid[0], getInsets().top + getInsets().bottom + sizeGrid[1]));
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

		assert(sizeGrid[0] == size && sizeGrid[1] == size);

		for(int y=0; y<size; y++) {
			for(int x=0; x<size; x++) {
				iRd.setRGB(x, y, Color.HSBtoRGB((float)(lC.getCell(x, y).getVals()[posVal]/1.0), 1f, 1f));
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