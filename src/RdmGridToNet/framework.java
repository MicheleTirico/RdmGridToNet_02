package RdmGridToNet;

import java.util.ArrayList;
import java.util.Random;

import javax.swing.JPanel;

import org.graphstream.algorithm.Kruskal;
import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.GraphPosLengthUtils;

import RdmGridToNet.framework.morphogen;

public abstract class framework  {
	
	protected static boolean isFeedBackModel;
	protected enum typeFeedbackModel { booleanSingleImpact , booleanCombinedImpact }
	protected static typeFeedbackModel  typeFeedbackModel  ;
	
	protected static layerSeed lSeed = new layerSeed();
	protected static layerRd lRd = new layerRd();
	protected static layerNet lNet = new layerNet() ;
	protected static  bucketSet bks = new bucketSet() ;
	protected static layerMaxLoc lMl = new layerMaxLoc();
	
	protected static String idPattern ;
	
	protected static int idNodeInt , idEdgeInt , idMaxLocInt; 
	protected static String idNode, idEdge , idMaxLoc ;
	protected static double  f , k  ;
	
	public enum typeRadius { square , circle}
	protected static typeRadius typeRadius;
	protected  ArrayList<cell> listCell = new ArrayList<cell> ();

	
	protected enum morphogen { a , b }		
	protected enum typeVectorField { gravity , slope , slopeDistance , slopeRadius , slopeDistanceRadius } 
	public enum RdmType { holes , solitions , movingSpots , pulsatingSolitions , mazes , U_SkateWorld , f055_k062 , chaos , spotsAndLoops , worms , waves }
	public enum typeNeighbourhood { moore, vonNewmann , m_vn }	

// GET METHODS --------------------------------------------------------------------------------------------------------------------------------------	
	// get spatial distance from 2 nodes 
	public static double getDistGeom ( Node n1 , Node n2 ) {	
		
		double [] 	coordN1 = GraphPosLengthUtils.nodePosition(n1) , 
					coordN2 = GraphPosLengthUtils.nodePosition(n2); 
		
		return  Math.pow(Math.pow( coordN1[0] - coordN2[0] , 2 ) + Math.pow( coordN1[1] - coordN2[1] , 2 ), 0.5 )  ;
	}
	
	public static double getDistGeom ( double [] coordN1 , double [] coordN2 ) {			
		return  Math.pow(Math.pow( coordN1[0] - coordN2[0] , 2 ) + Math.pow( coordN1[1] - coordN2[1] , 2 ), 0.5 )  ;
	}

// INITIALIZATION METHODS ---------------------------------------------------------------------------------------------------------------------------
	// initialize world with more than 1 circle and corresponding Rdm pulse 
	public static void initCircle ( double valA , double valB , int numNodes , int centreX ,int centreY , int radiusRd , int radiusNet ) {	
		lRd.setValueOfCellAround(valA, valB, centreX, centreY, radiusRd);		
		if ( isFeedBackModel)
			lSeed.initSeedCircleFeedBack (numNodes, radiusNet, centreX,centreY );
		else 
			lSeed.initSeedCircle(numNodes, radiusNet, centreX,centreY );
	}
	
	/** 
	 * initialize with multi circle in random position 
	 */
	public static void initMultiCircle ( int seedRd , int numMaxCircle , int numNodes , double distMinBord , double radiusCircle , double valA , double valB ) {
		Random rd = new Random( seedRd );
		int numCircle = 0 ;
		int[] sizeGrid = lRd.getSizeGrid();
		
		while ( numCircle < numMaxCircle ) {
			double centreX =  distMinBord +  ( sizeGrid[0] - 2 * distMinBord )  *rd.nextDouble() ,
					centreY =  distMinBord +  ( sizeGrid[1] - 2*  distMinBord )  *rd.nextDouble();
					
			cell c = lRd.getCell(new double[] {centreX ,centreY }) ;
			c.setVals(valA, valB);
			lSeed.initSeedCircle(numNodes, radiusCircle , centreX,centreY );
			numCircle ++;
		}
		
	}
	
	/**
	 *  initialization with a given number of nodes in random position  
	 *  also set val of perturbation 
	 */
	public static void initRandomPoint ( int seedRd , int numMaxPoint , morphogen m , double valMorp ) {
		Random rd = new Random( seedRd );
		Graph graph = lNet.getGraph() ;
		ArrayList <cell> listCell = lRd.getListCell()   ;
		int numNodes  = 0 ,
				sizeListCell = listCell.size();
		while (  numNodes < numMaxPoint ) {
			cell c = listCell.get((int) ( sizeListCell * rd.nextDouble() ));	
			idNode =  Integer.toString(idNodeInt++) ;
			graph.addNode(idNode) ;
			Node n = graph.getNode(idNode);			
			n.addAttribute("xyz", c.getX() ,  c.getY() , 0 );			
			lSeed.createSeed( c.getX() ,  c.getY()  , n );
			if ( m.equals(morphogen.a))
				c.setVal1(valMorp);
			else 
				c.setVal2(valMorp);
			numNodes++;
		}
	}
	public static void initMultiFistFul (  int seedRd ,  int numMaxFistful , int numNodesEachFistFul ,  double radius , morphogen m , double valMorp ) {
		Random rd = new Random( seedRd );
		int numFistful = 0 , seedRdStruct = seedRd ;
		int[] sizeGrid = lRd.getSizeGrid();
		while ( numFistful < numMaxFistful) {
			double centreX =  radius +  ( sizeGrid[0] - 2 * radius )  *rd.nextDouble() ,
					centreY =  radius +  ( sizeGrid[1] - 2*  radius )  *rd.nextDouble();
			
			initfistfulOfNodes(seedRdStruct, numNodesEachFistFul, new double[] {centreX ,centreY } , radius, m, valMorp);
			seedRdStruct ++ ;
			numFistful++;
		}
	}
	
	public static void initMultiFistFul (  int seedRd ,  int numMaxFistful , int numNodesEachFistFul ,  double radius , double bordNoCentre ,  morphogen m , double valMorp ) {
		Random rd = new Random( seedRd );
		int numFistful = 0 , seedRdStruct = seedRd ;
		int[] sizeGrid = lRd.getSizeGrid();
		while ( numFistful < numMaxFistful) {
			double centreX =  bordNoCentre +  ( sizeGrid[0] - 2 * bordNoCentre )  *rd.nextDouble() ,
					centreY =  bordNoCentre +  ( sizeGrid[1] - 2* bordNoCentre )  *rd.nextDouble();
		
			initfistfulOfNodes(seedRdStruct, numNodesEachFistFul, new double[] {centreX ,centreY } , radius, m, valMorp);
			seedRdStruct ++ ;
			numFistful++;
		}
	}
	
	/**
	 * create a given number of nodes around center 
	 * @param seedRd
	 * @param numMaxNodes
	 * @param cooordsCentre
	 * @param radius
	 * @param m
	 * @param valMorp
	 */
	public static void initfistfulOfNodes ( int seedRd , int numMaxNodes , double[] cooordsCentre , double radius , morphogen m , double valMorp ) {
		Random rd = new Random( seedRd );
		Graph graph = lNet.getGraph() ;
		int numNodes = 0 ;
		while ( numNodes < numMaxNodes) {
			double coordX = cooordsCentre[0] + radius * 2 * rd.nextDouble() - radius  ,
					 coordY = cooordsCentre[1] + radius * 2 * rd.nextDouble() - radius ; 
			idNode =  Integer.toString(idNodeInt++) ;
			graph.addNode(idNode) ;
			Node n = graph.getNode(idNode);			
			n.addAttribute("xyz", coordX,  coordY, 0 );			
			lSeed.createSeed( coordX,  coordY , n );
			
			numNodes++ ; 
		}
		if ( m.equals(morphogen.a))
			lRd.getCell(cooordsCentre).setVal1(valMorp);
		else 
			lRd.getCell(cooordsCentre).setVal2(valMorp);
		
//		for ( Node n1 : graph.getEachNode()) {
//			for ( Node n2 : graph.getEachNode()) {
//				if ( ! n1.equals(n2)) {
//					try {
//						idEdge = Integer.toString(idEdgeInt+1 );
//						Edge e = graph.addEdge(idEdge, n1, n2);
//						e.addAttribute("length", getDistGeom(n1,n2));
//						idEdgeInt++;
//					}catch (EdgeRejectedException e) {
//						// TODO: handle exception
//					}
//				}
//			}		
//		}
//		
//		// compute Krustal algoritm
//		Kruskal kruskal = new Kruskal( "tree" , true , false ) ;
//		kruskal.init(graph) ;
//		kruskal.compute();	
//		System.out.println(graph.getEdgeCount());
//		for ( Edge e : graph.getEachEdge()) {
//			boolean tree = e.getAttribute("tree");	
//			
////			if ( tree == true ) 		System.out.println(tree);
//			if ( tree == false  ) 
//				graph.removeEdge(e);
//		}	
//		System.out.println(graph.getEdgeCount());
	}
	
// COMMON METHODS -----------------------------------------------------------------------------------------------------------------------------------
	// set RD start values to use in similtion ( gsAlgo )
	protected static void setRdType ( RdmType pattern ) {
		
		switch ( pattern ) {
			case holes: 				{ f = 0.039 ; k = 0.058 ; } 
										break ;
			case solitions :			{ f = 0.030 ; k = 0.062 ; } 
										break ; 
			case mazes : 				{ f = 0.029 ; k = 0.057 ; } 
										break ;
			case movingSpots :			{ f = 0.014 ; k = 0.054 ; } 
										break ;
			case pulsatingSolitions :	{ f = 0.025 ; k = 0.060 ; } 
										break ;
			case U_SkateWorld :			{ f = 0.062 ; k = 0.061 ; } 
										break ;
			case f055_k062 :			{ f = 0.055 ; k = 0.062 ; } 
										break ;
			case chaos :				{ f = 0.026 ; k = 0.051 ; } 
										break ;
			case spotsAndLoops :		{ f = 0.018 ; k = 0.051 ; } 
										break ;
			case worms :				{ f = 0.078 ; k = 0.061 ; } 
										break ;
			case waves :				{ f = 0.014 ; k = 0.045 ; } 
										break ;		
		}	
		idPattern = pattern.toString();	
	}
	
	public static void isFeedBackModel ( boolean isFbM , typeFeedbackModel  type ) {
		isFeedBackModel = isFbM ;
		typeFeedbackModel  = type  ; 
	}	
}
