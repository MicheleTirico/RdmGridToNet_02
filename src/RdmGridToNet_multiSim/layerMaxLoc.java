package RdmGridToNet_multiSim;

import java.util.ArrayList;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

public class layerMaxLoc extends framework {
	
	private ArrayList<cell> listMaxLocal = new ArrayList<cell>() ;		
	private Graph graphMaxLoc = new SingleGraph ("maxLoc");
	private boolean compute , vizLayer;
	private morphogen m ;
	

	private typeInit  typeInit ;
	private typeComp typeComp ;
	
	public layerMaxLoc () {
		this(false, false ,null , null, null) ;
	}
	
	public layerMaxLoc ( boolean compute , boolean vizLayer , typeInit  typeInit , typeComp typeComp , morphogen m ) {
		this.compute = compute ;
		this.vizLayer = vizLayer;
		this.typeInit = typeInit ;
		this.typeComp = typeComp ;	
		this .m = m ;
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
			cell c = lRd.getCell(b);
			double val = lRd.getValMorp(c, m , false);
			double valTest = 0 ;
			int ind = 0;
			ArrayList<cell> listNeig = lRd.getListNeighbors(typeNeighbourhood.moore, c.getX(), c.getY()) ;
			while( valTest < val && ind < listNeig.size()) {
				valTest = lRd.getValMorp(listNeig.get(ind), m, false);
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
				
		for ( cell c : listCell) {
			double val = lRd.getValMorp(c, m , false);
			double valTest = 0 ;
			int ind = 0;
			ArrayList<cell> listNeig = lRd.getListNeighbors(typeNeighbourhood.moore, c.getX(), c.getY()) ;
			while( valTest < val && ind < listNeig.size()) {
				valTest = lRd.getValMorp(listNeig.get(ind), m, false);
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
		c.setMaxLocal(false);
	}	
	
	// add max local from list and set val at corresponding cell
	private void addMaxLocal ( cell c  ) {
		listMaxLocal.add(c);
		c.setMaxLocal(true);
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
