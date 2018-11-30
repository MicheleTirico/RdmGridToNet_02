package RdmGridToNetMultiLayer;

import java.util.ArrayList;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import RdmGridToNetMultiLayer.layerCell.typeNeighbourhood;


public class layerMaxLoc extends framework {
	
	private ArrayList<cell> listMaxLocal = new ArrayList<cell>() ;		
	private Graph graphMaxLoc = new SingleGraph ("maxLoc");
	private boolean compute , vizLayer;

	protected static int idMaxLocInt; 
	protected static String idMaxLoc ;
	private layerCell lCell ;
	private int posVal  ;
	
	public enum typeInit {test, // centreRd , allPointActive , noInizialization
		};
	
	public enum typeComp {test, wholeGrid , aroundNetGraph };
	private typeInit  typeInit ;
	private typeComp typeComp ;
	
	public layerMaxLoc () {
		this(false, null, false, null, null, 0);
	}
	
	public layerMaxLoc ( boolean compute , layerCell lCell ,  boolean vizLayer , typeInit  typeInit , typeComp typeComp ,  int posVal  ) {
		this.compute = compute ;
		this.lCell = lCell ;
		this.vizLayer = vizLayer;
		this.typeInit = typeInit ;
		this.typeComp = typeComp ;	
		this.posVal = posVal ;
	}
	
// INITIALIZE LAYER ---------------------------------------------------------------------------------------------------------------------------------
	public void initializeLayer ( ) {
		switch (typeInit) {
		case test: {
		} 	break;
		//for other initialization , see layerRd_old_01 
		}
	}
	
// COMPUTE LAYER ------------------------------------------------------------------------------------------------------------------------------------
	public void updateLayer () {
		if ( compute) {
		//	System.out.println("numberMaxLo " + getNumMaxLoc());
			switch (typeComp) {
				case wholeGrid :
					computeLayerWholeGrid();
					break;
				case aroundNetGraph :
					computeLayerAroundNetGraph();
				default:
					break;
			}
		}
		if ( vizLayer)
			createGraph();
	}
	
	private void computeLayerAroundNetGraph ( ) {
		ArrayList<cell>  listCellToAdd = new ArrayList<cell>() ,
				listCellToRemove = new ArrayList<cell>() ;
		
		for ( bucket b : bks.getListBucketNotEmpty() ) {
			cell c = lCell.getCell(b);
			double  val = lCell.getCellVals(c)[posVal];
			
			double valTest = 0 ;
			int ind = 0;
			ArrayList<cell> listNeig = lCell.getListNeighbors(typeNeighbourhood.moore, c.getX(), c.getY()) ;
			while( valTest < val && ind < listNeig.size()) {
				valTest =  lCell.getCellVals(listNeig.get(ind))[posVal];
				ind++;	
			}
			if ( ind == listNeig.size() && ! listMaxLocal.contains(c) ) 	
				listCellToAdd.add(c);		
			else 
				listCellToRemove.add(c);	
		}
		
		listCellToAdd.stream().forEach(c -> addMaxLocal(c));
		listCellToRemove.stream().forEach(c -> removeMaxLocal(c));
	}
	
	private void computeLayerWholeGrid () {
		ArrayList<cell>  listCellToAdd = new ArrayList<cell>() ,
				listCellToRemove = new ArrayList<cell>() ;
				
		for ( cell c : lCell.getListCell()) {
			double val = lCell.getCellVals(c)[posVal];
			double valTest = 0 ;
			int ind = 0;
			ArrayList<cell> listNeig = lCell.getListNeighbors(typeNeighbourhood.moore, c.getX(), c.getY()) ;
			while( valTest < val && ind < listNeig.size()) {
				valTest = lCell.getCellVals(listNeig.get(ind))[posVal]; 
				ind++;	
			}
			if ( ind == listNeig.size() && ! listMaxLocal.contains(c) ) 	
				listCellToAdd.add(c);		
			else 
				listCellToRemove.add(c);	
		}
		
		listCellToAdd.stream().forEach(c -> addMaxLocal(c));
		listCellToRemove.stream().forEach(c -> removeMaxLocal(c));
	}
	
// ADD AND REVOVE MAX LOCAL -------------------------------------------------------------------------------------------------------------------------
	// remove max local from list and set val at corresponding cell
	private void removeMaxLocal ( cell c ) {
		listMaxLocal.remove(c);
		c.setIsTest(0, false);
	}	
	
	// add max local from list and set val at corresponding cell
	private void addMaxLocal ( cell c  ) {
		listMaxLocal.add(c);
		c.setIsTest(0, true);
	}

// VIZ LAYER ----------------------------------------------------------------------------------------------------------------------------------------
	private void createNode ( cell c ) {
		idMaxLoc = Integer.toString(idMaxLocInt);
		Node n = graphMaxLoc.addNode(idMaxLoc);
		n.addAttribute("xyz", c.getX(), c.getY() , 0 );
		idMaxLocInt++;
	}
	
	private void createGraph ( ) {	
		for ( Node n : graphMaxLoc.getEachNode())
			graphMaxLoc.removeNode(n);	
		for ( cell c : listMaxLocal ) {
			createNode(c);
		}
	}
	
// GET METHODS --------------------------------------------------------------------------------------------------------------------------------------
	public ArrayList<cell> getListMaxLoc ( ) {
		return listMaxLocal;
	}
	
	public int getNumMaxLoc () {
		return listMaxLocal.size();
	}	
	
	public Graph getGraph () {
		return graphMaxLoc;
	}
}
