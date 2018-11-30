package RdmGridToNet_multiSim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.GraphPosLengthUtils;

public class layerSeed extends framework {
	
	private ArrayList<seed> seeds = new ArrayList<seed>();
	private double alfa, dist, g , Ds , r ;
	private morphogen m ;

	protected static handleLimitBehaviur handleLimitBehaviur ;
	private static typeInitializationSeed typeInitializationSeed ;

	private bucketSet bks = getBucketSet();

	public layerSeed () {
		this( 0, null , 2 , null );	
	}

	public layerSeed ( double r , morphogen m , double alfa, handleLimitBehaviur handleLimitBehaviur) {
		this.r = r ;		
		this.m = m ; 
		this.alfa = alfa ;
		this.handleLimitBehaviur = handleLimitBehaviur ;
	}
	
	public void setupGravityLayer ( double g , double alfa , double Ds ) {
		this.g = g;
		this.alfa = alfa ;
		this.Ds = Ds ; 
	}

	
// INITIALIZATION SEED SET --------------------------------------------------------------------------------------------------------------------------
	public void initializationSeedThMorp ( morphogen m , double minTh, double maxTh) {
		for ( cell c : listCell ) {
			double val = lRd.getValMorp(c, m, true) ;
			if (val >= minTh && val <= maxTh)
				createSeed(c.getX(), c.getY());
		}
	}
	
	public void initializationEachNode () {
		for ( Node n : lNet.getGraph().getEachNode() ) {
			double[] pos = GraphPosLengthUtils.nodePosition(n) ;
			createSeed(pos[0], pos[1]);
		}
	}
	
	public void initializationSeedCircle ( int numNodes , double radius ) {
		
		Graph graph = lNet.getGraph() ;
		double[] centerLayerRd = lRd.getCenter () ;
		double 	centerX = centerLayerRd[0] , 
				centerY = centerLayerRd[1] ,
				angle = 2 * Math.PI / numNodes ;		
		for ( idNodeInt = 0 ; idNodeInt < numNodes ; idNodeInt++ ) {
			
			double 	coordX = radius * Math.cos( idNodeInt * angle ) ,
					coordY = radius * Math.sin( idNodeInt * angle ) ;
					
			idNode =  Integer.toString(idNodeInt) ;
			graph.addNode(idNode) ;
			Node n = graph.getNode(idNode);
			
			n.addAttribute("xyz", centerX + coordX ,  centerY + coordY , 0 );
			
			lSeed.createSeed(centerX + coordX, centerY + coordY , n);
		}
		
		for ( idEdgeInt = 0 ; idEdgeInt < numNodes ; idEdgeInt++ ) {
			String idEdge = Integer.toString(idEdgeInt);
			try {		
				graph.addEdge(idEdge,Integer.toString(idEdgeInt) , Integer.toString(idEdgeInt+1) ) ;		
			}
			catch (org.graphstream.graph.ElementNotFoundException e) {
				graph.addEdge(idEdge,Integer.toString(idEdgeInt) , Integer.toString(0) ) ;
				break ; 
			}
		}
		for ( Node n : graph.getEachNode()) {
			System.out.println(n);
			bks.putNode(n);
		}

	}

	public void initializationSeedCircle ( int numNodes , double radius , double centerX , double centerY) {		
 		Graph graph = lNet.getGraph() ;
		double angle = 2 * Math.PI / numNodes ;		
		int nodeCount = graph.getNodeCount() ;
		
		Node old = null  ;
		ArrayList<Node> listNodes = new ArrayList<Node>();
		
		for ( idNodeInt = nodeCount  ; idNodeInt <nodeCount + numNodes ; idNodeInt++ ) {
			
			double 	coordX = radius * Math.cos( idNodeInt * angle ) ,
					coordY = radius * Math.sin( idNodeInt * angle ) ;
					
			idNode =  Integer.toString(idNodeInt) ;
			graph.addNode(idNode) ;
			Node n = graph.getNode(idNode);
			
			n.addAttribute("xyz", centerX + coordX ,  centerY + coordY , 0 );
			listNodes.add(n);
			lSeed.createSeed(centerX + coordX, centerY + coordY , n);
	
			try {
				idEdge = Integer.toString(idEdgeInt+1 );
				Edge e = graph.addEdge(idEdge, n, old);
				e.addAttribute("length", getDistGeom(n,old));
				idEdgeInt++;
			} catch (NullPointerException e)  {			//		e.printStackTrace();
			
			}
			old = n ;
		}	
		idEdge = Integer.toString(idEdgeInt+1 );
		Edge e = graph.addEdge(idEdge, old, listNodes.get(0));
		e.addAttribute("length", getDistGeom(listNodes.get(0),old));
		idEdgeInt++;
		 
		for ( Node n : graph.getEachNode())  {
			System.out.println(bks);
			bks.putNode(n);	
		}
	}
	
	public void initializationSeedCircleFeedBack ( int numNodes , double radius , double centerX , double centerY  ) {		
 		Graph graph = lNet.getGraph() ;
		double angle = 2 * Math.PI / numNodes ;		
		int nodeCount = graph.getNodeCount() ;
		
		Node old = null  ;
		ArrayList<Node> listNodes = new ArrayList<Node>();
		
		for ( idNodeInt = nodeCount  ; idNodeInt <nodeCount + numNodes ; idNodeInt++ ) {
			
			double 	coordX = radius * Math.cos( idNodeInt * angle ) ,
					coordY = radius * Math.sin( idNodeInt * angle ) ;
					
			idNode =  Integer.toString(idNodeInt) ;
			graph.addNode(idNode) ;
			Node n = graph.getNode(idNode);
			
			n.addAttribute("xyz", centerX + coordX ,  centerY + coordY , 0 );
			listNodes.add(n);
			lSeed.createSeed(centerX + coordX, centerY + coordY , n);
			
	
			try {
				idEdge = Integer.toString(idEdgeInt+1 );
				Edge e = graph.addEdge(idEdge, n, old);
				e.addAttribute("length", getDistGeom(n,old));
				idEdgeInt++;
			} catch (NullPointerException e)  {			//		e.printStackTrace();
			
			}
			old = n ;
		}	
		idEdge = Integer.toString(idEdgeInt+1 );
		Edge e = graph.addEdge(idEdge, old, listNodes.get(0));
		e.addAttribute("length", getDistGeom(listNodes.get(0),old));
		idEdgeInt++;
		 
		for ( seed s : lSeed.getListSeeds())
			lSeed.setSeedInCell(s, true);
		
		for ( Node n : graph.getEachNode()) {
			lNet.setNodeInCell(n, true);
			bks.putNode(n);	
		}
	}
	
	
// REMOVE SEED METHODS ------------------------------------------------------------------------------------------------------------------------------
	public void removeSeed ( seed s ) {
		seeds.remove(s) ;
	}
	
	public void removeSeed ( seed s , boolean removeSeedFromCell ) {
		seeds.remove(s) ;
		lRd.getCell(s).setHasSeed(false);
	}
	
	public void removeSeed ( seed s , boolean removeSeedFromCell , layerRd.typeNeighbourhood typeNeighbourhood ) {
		seeds.remove(s) ;
		cell c = 	lRd.getCell(s) ;
		c.setHasSeed(false);
		for ( cell ce : lRd.getListNeighbors(typeNeighbourhood, c.getX(), c.getY()))
			ce.setHasSeed(false);
	}

// CREATE SEED METHODS ------------------------------------------------------------------------------------------------------------------------------
	public void createSeed ( double X , double Y ) {
		seeds.add( new seed(X, Y, 0, 0 , null) ) ;
	}
	
	public void createSeed ( double X , double Y , Node n  ) {
		n.addAttribute("xyz", X , Y  , 0 );
		seeds.add( new seed(X, Y, 0, 0 , n) ) ;
	}

	public void createSeed ( double X , double Y , Node n , boolean setSeedInCell ) {
		n.addAttribute("xyz", X , Y  , 0 );
		seed s = new seed(X, Y, 0, 0 , n )  ;
		seeds.add(s) ;
		lSeed.setSeedInCell(s, setSeedInCell);
	}
	
	
// FEEDBACK -----------------------------------------------------------------------------------------------------------------------------------------
	public void setSeedInCell ( seed s , boolean setSeedInCell )  {
		try {
			cell c = lRd.getCell(s);
			c.setHasSeed(setSeedInCell);
		} catch (NullPointerException e) {
		}
	}
		
		
// UPDATE LAYER SEED --------------------------------------------------------------------------------------------------------------------------------	
	public void updateLayer() {
		for (seed s : seeds) {
			double sX = s.getX() , sY = s.getY() , vecX = 0 , vecY = 0;
			for ( int x = (int) Math.floor(s.getX() -r ) ; x <= (int) Math.ceil(s.getX() + r ); x++ )
				for ( int y = (int) Math.floor(s.getY() -r ) ; y <= (int) Math.ceil(s.getY() + r ); y++ ) {
					try {
						cell c = lRd.getCell(x,y);
						double val = lRd.getValMorp(c, m, true) ;
						vecX = vecX + ceckPositionVectorGravity(c.getX(),s.getX(),val) ;
						vecY = vecY + ceckPositionVectorGravity(c.getY(),s.getY(),val) ;
					} catch (ArrayIndexOutOfBoundsException e) {
						continue ;
					}
				
				}
			vecX = checkValueVector(vecX, 1) ;
			vecY = checkValueVector(vecY, 1) ;
			
			s.setVec(-vecX, -vecY);	
			s.setCoords(sX - vecX , sY - vecY);	
		}
	}

	protected double[] getVector (seed s , typeVectorField typeVectorField  , typeRadius typeRadius) {	
		double[] vector = new double[2];
		switch ( typeVectorField) {
			case gravity:
				vector = getVectorGravity(s,typeRadius);
				break;
			case slope :
				vector = getVectorSlope(s,typeRadius);
				break;
			case slopeDistance :
				vector = getVectorSlopeDistance(s,typeRadius);
				break;
			case slopeRadius :
				vector = getVectorSlopeRadius(s,typeRadius);
				break;
			case slopeDistanceRadius :
				vector = getVectorSlopeDistanceRadius(s,typeRadius);
				break;
		}
		return vector ;
	}
	
	// get vector slope distance
	private double[] getVectorSlopeDistance ( seed s,typeRadius typeRadius ) {
		double sX = s.getX() , sY = s.getY() ;

		cell 	c00 = lRd.getCell( (int) Math.floor(sX),(int) Math.floor(sY)), 
				c11 = lRd.getCell( (int) Math.ceil(sX),(int) Math.ceil(sY)),
				c01 = lRd.getCell( (int) Math.floor(sX),(int) Math.ceil(sY)),
				c10 = lRd.getCell( (int) Math.ceil(sX),(int) Math.floor(sY));
		
		double 	val00 = lRd.getValMorp(c00, m, true) , 
				val11 = lRd.getValMorp(c11, m, true) ,
				val01 = lRd.getValMorp(c01, m, true) ,
				val10 = lRd.getValMorp(c10, m, true) ,

				distXfloor = Math.pow(1+Math.abs(sY - Math.floor(sY)), 2), 
				distXceil = Math.pow(1+Math.abs(sY - Math.ceil(sY)), 2), 
				
				distYfloor = Math.pow(1+Math.abs(sX - Math.floor(sX)), 2), 
				distYceil =  Math.pow(1+Math.abs(sX - Math.ceil(sX)), 2); 
		
		double 	vecX = ( val10 - val00) / distYfloor + ( val11 - val01 ) / distYceil ,
				vecY = ( val01 - val00) / distXfloor + ( val11 - val10 ) / distXceil ;
					
		if ( Double.isNaN(vecX))			vecX = 0 ;
		if ( Double.isNaN(vecY))			vecY = 0 ;

		s.setVec(-vecX, -vecY);
		return new double[] {-vecX ,-vecY} ;
	}
		
	// get vector slope radius
	private double[] getVectorSlopeRadius ( seed s ,typeRadius typeRadius) {
		double sX = s.getX() , sY = s.getY() , vecX = 0 , vecY = 0 ;
		for ( int x = (int) Math.floor(s.getX() -r ) ; x <= (int) Math.ceil(s.getX() + r ); x++ )
			for ( int y = (int) Math.floor(s.getY() -r ) ; y <= (int) Math.ceil(s.getY() + r ); y++ ) {		
				
				cell c = lRd.getCell(x,y);
				if ( typeRadius.equals(typeRadius.circle)) 
					if ( Math.pow(Math.pow(c.getX() - s.getX(), 2) + Math.pow(c.getY() - s.getY(), 2),0.5) > r ) 
						continue ;
				
				double 	addVecX = lRd.getValMorp(lRd.getCell(x+1,y), m, true) - lRd.getValMorp(lRd.getCell(x-1,y), m, true) , 
						addVecY = lRd.getValMorp(lRd.getCell(x,y+1), m, true) - lRd.getValMorp(lRd.getCell(x,y-1), m, true) ;
					
				vecX = vecX + addVecX ;
				vecY = vecY + addVecY ;			
			}
		vecX = checkValueVector2(vecX, .50) ;
		vecY = checkValueVector2(vecY, .50) ;
		 
		s.setVec( -vecX, -vecY);
		return new double[] {-vecX ,-vecY} ;
	}
	
	// get vector slope distance radius
	private double[] getVectorSlopeDistanceRadius ( seed s ,typeRadius typeRadius) {
		double sX = s.getX() , sY = s.getY() , vecX = 0 , vecY = 0 ;
		for ( int x = (int) Math.floor(s.getX() -r ) ; x <= (int) Math.ceil(s.getX() + r ); x++ )
			for ( int y = (int) Math.floor(s.getY() -r ) ; y <= (int) Math.ceil(s.getY() + r ); y++ ) {	
				try {
					cell c = lRd.getCell(x,y);
					if ( typeRadius.equals(typeRadius.circle)) 
						if ( Math.pow(Math.pow(c.getX() - s.getX(), 2) + Math.pow(c.getY() - s.getY(), 2),0.5) > r ) 
							continue ;
				
					double 	distX = Math.pow(1+Math.abs(sY - y), alfa) ,
							distY = Math.pow(1+Math.abs(sX - x), alfa); 
			
					double 	addVecX = ( lRd.getValMorp(lRd.getCell(x+1,y), m, true) - lRd.getValMorp(lRd.getCell(x-1,y), m, true) ) / distY , 
							addVecY = ( lRd.getValMorp(lRd.getCell(x,y+1), m, true) - lRd.getValMorp(lRd.getCell(x,y-1), m, true) ) / distX ;
					
					vecX = vecX + addVecX ;
					vecY = vecY + addVecY ;		
					
					if ( Double.isNaN(vecX))			vecX = 0 ;
					if ( Double.isNaN(vecY))			vecY = 0 ;
				} catch (NullPointerException e) {
				}	
			}
			
		vecX = checkValueVector2(vecX, .1) ;
		vecY = checkValueVector2(vecY, .1) ;
		
		s.setVec( -vecX, -vecY);
		return new double[] {-vecX ,-vecY} ;
	}
		
	// get vector slope
	private double[] getVectorSlope ( seed s ,typeRadius typeRadius) {
		double sX = s.getX() , sY = s.getY() ;

		cell 	c00 = lRd.getCell( (int) Math.floor(sX),(int) Math.floor(sY)), 
				c11 = lRd.getCell( (int) Math.ceil(sX),(int) Math.ceil(sY)),
				c01 = lRd.getCell( (int) Math.floor(sX),(int) Math.ceil(sY)),
				c10 = lRd.getCell( (int) Math.ceil(sX),(int) Math.floor(sY));
		
		double 	val00 = lRd.getValMorp(c00, m, false) , 
				val11 = lRd.getValMorp(c11, m, false) ,
				val01 = lRd.getValMorp(c01, m, false) ,
				val10 = lRd.getValMorp(c10, m, false) ;
			
		double 	vecX = val10 - val00 + val11 - val01 ,
				vecY = val01- val00 + val11 - val10 ;
		
		vecX = checkValueVector2(vecX, .1 ) ;
		vecY = checkValueVector2(vecY, .1 ) ;
							
		s.setVec(-vecX, -vecY);
		return new double[] {-vecX ,-vecY} ;
	}
	
	//get vector gravity
	private double[] getVectorGravity ( seed s ,typeRadius typeRadius) {
		double vecX = 0 , vecY = 0;
		for ( int x = (int) Math.floor(s.getX() -r ) ; x <= (int) Math.ceil(s.getX() + r ); x++ )
			for ( int y = (int) Math.floor(s.getY() -r ) ; y <= (int) Math.ceil(s.getY() + r ); y++ ) {
				try {
					cell c = lRd.getCell(x,y);
					if ( typeRadius.equals(typeRadius.circle)) 
						if ( Math.pow(Math.pow(c.getX() - s.getX(), 2) + Math.pow(c.getY() - s.getY(), 2),0.5) > r ) 
							continue ;
						
					double val = lRd.getValMorp(c, m, true) ;
					vecX = vecX + ceckPositionVectorGravity(c.getX(),s.getX(),val) ;
					vecY = vecY + ceckPositionVectorGravity(c.getY(),s.getY(),val) ;
				} catch (ArrayIndexOutOfBoundsException e) {
					continue ;
				}
			}
		vecX = checkValueVector2(vecX, 0.1) ;
		vecY = checkValueVector2(vecY, 0.1) ;
		
		s.setVec(-vecX, -vecY);
		return new double[] {-vecX ,-vecY} ;
	}
	
	private void setSeedNewCoords ( seed s , double vecX ,double vecY ) {
		s.setCoords(s.getX() + vecX, s.getY() + vecY);
	}
	
	// check max value of vector 
	private double checkValueVector (double vec , double valMax) { 
		if (vec > valMax )				
			return  valMax ;
		else if (vec < - valMax )				
			return - valMax ;
		else 
			return vec;
	}
	
	// check max value of vector 
	private double checkValueVector2 (double vec , double valMax) { 
		double 	vecAbs = Math.abs(vec) , 
				valMaxAbs = Math.abs(valMax);
		
		if ( vecAbs > valMaxAbs )
			if ( vec > 0.0 )
				return valMax;
			else 
				return -valMax ;
		else 
			return vec;
	}
	
	// not used
	private double ceckPositionVector (double vector ,double posCell , double posSeed, double val) {
		vector = vector / Math.pow(posCell - posSeed, alfa) ;	
		if ( posCell == posSeed)
			return 0 ;
		if ( posCell < posSeed)
			return -vector ;
		else return vector ;
	}
	
	// check distance between seed and cell
	private double ceckPositionVectorGravity (double posCell , double posSeed, double val)  {
		
		double v = Ds * g * val / Math.pow(1 + Math.abs(posCell - posSeed), alfa);
		if ( posCell == posSeed)
			return 0 ;
		if ( posCell < posSeed)
			return -v ;
		else return v ;
	}
	
	// old method
	public void updateLaye_01() {
		for ( seed s : seeds ) {
			double sX = s.getX() , sY = s.getY() , vecX = 0 , vecY = 0;		
			ArrayList<cell> listCell = new ArrayList<cell>(getListCellsInRadius(s));
			for ( cell c : listCell) {
				double 	val = lRd.getValMorp(c, m, false) ;
				if ( val > 1 )				val = 1 ; 
				vecX = vecX + ceckPositionVectorGravity(c.getX(),s.getX(),val) ;
				vecY = vecY + ceckPositionVectorGravity(c.getY(),s.getY(),val) ;
			}
			if (vecX > 1 )				vecX = 1 ;		
			if (vecY > 1 )				vecY = 1 ;		
			if (vecX < - 1 )			vecX = - 1 ;		
			if (vecY < - 1 )			vecY = - 1 ;
			
			s.setVec(vecX, vecY);
			s.setX( sX + vecX );
			s.setY( sY + vecY );	
		}
	}
	
	// not used
	private double[] computeVec (seed s, morphogen m ) {
		double vecX = 0 , vecY = 0, sX = s.getX(), sY = s.getY() ;	
		for ( int x = (int) Math.floor(s.getX() - r ) ; x <= (int) Math.ceil(s.getX() + r ); x++ )
			for ( int y = (int) Math.floor(s.getY() - r ) ; y <= (int) Math.ceil(s.getY() + r ); y++ ) {
				double 	val = lRd.getValMorp(lRd.getCell(x, y), m, false) ;
				if ( val > 1 )
					val = 1 ;
				
				vecX = vecX + getVector(x, sX, val);
				vecY = vecY + getVector(y, sY, val) ;			
			}
		double[] vec = new double[2];
		vec[0] = vecX;
		vec[1] = vecY;
		return vec; 
	}
	
	// not used
	private  ArrayList<cell> getListCellsInRadius ( seed s  ) {	
		ArrayList<cell> list = new ArrayList<cell> () ;
		for ( int x = (int) Math.floor(s.getX() -r ) ; x <= (int) Math.ceil(s.getX() + r ); x++ )
			for ( int y = (int) Math.floor(s.getY() -r ) ; y <= (int) Math.ceil(s.getY() + r ); y++ ) 
				list.add(lRd.getCell(x,y));		
		return list;
	}
	
	// not used	
	private double getVector (double posCell, double posSeed, double val ) {	
		double sign = getSignVec(posCell , posSeed ) ;	
		if ( sign == 0 ) 
			return 0 ;
		else return sign * ( g * Ds * val ) / Math.pow(posCell - posSeed, alfa) ;		
	}
	
	// not used
	private double getSignVec ( double pos1 , double pos2 ) {	
		if (pos1 == pos2)
			return 0 ; 
		else if ( pos1 < pos2   )
			return -1;
		else 
			return 1 ;
	}
	
// GET METHODS --------------------------------------------------------------------------------------------------------------------------------------
	public ArrayList<seed> getListSeeds () {
		return seeds;		
	}
	
	public int getNumSeeds () {
		return seeds.size();
	}
	
	public ArrayList<Node> getListNodeWithSeed ( ) {
		ArrayList<Node> list = new ArrayList<Node> ();
		for ( seed s : seeds) {
			Node n = s.getNode();
			if ( ! list.contains(n))
				list.add(s.getNode());
		}
		return list;
	}
	
	public Map<cell ,Double> getValAroundSeed ( seed s , morphogen m , double r ) {
		Map<cell ,Double> map = new HashMap<cell ,Double> ();
		for ( int x = (int) Math.floor(s.getX() -r ) ; x <= (int) Math.ceil(s.getX() + r ); x++ )
			for ( int y = (int) Math.floor(s.getY() - r ) ; y <= (int) Math.ceil(s.getY() + r ); y++ ) {
					cell c = lRd.getCell(x,y);
					double val = lRd.getValMorp(c, m, true) ;
					map.put(c, val);
				}	
		return map;
	}
}
