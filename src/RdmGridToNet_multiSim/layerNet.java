package RdmGridToNet_multiSim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.graphstream.algorithm.Dijkstra;
import org.graphstream.algorithm.Dijkstra.Element;
import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.graphicGraph.GraphPosLengthUtils;

import RdmGridToNet.layerSeed.handleLimitBehaviur;
import scala.languageFeature.reflectiveCalls;

public class layerNet extends framework {
 
	private int numNodes  ;
	private double radius;
	private String id ; 
	public enum typeSetupLayer { circle , test }
	protected boolean seedHasReachLimit ;
	private static typeSetupLayer typeSetupLayer ;
	
	private Graph graph = new SingleGraph ( "net" );

	public layerNet () {
		this( null ) ;
	}
	
	public layerNet(String id) {
		this.id = id ;
	}
	
	public void setLengthEdges ( String attr , boolean setLengthEdges) {
		if ( setLengthEdges ) 
			for ( Edge e : graph.getEachEdge() ) 
				e.addAttribute("length", getLength(e));	
	}
	
	public void setParametersCircle ( int numNodes, double radius ) {
		this.numNodes = numNodes ;
		this.radius = radius ;
	}
		
	public void test ( ) {
		for ( Node n : graph.getEachNode()) 
			bks.putNode(n);	
		System.out.println(bks.getListBucketNotEmpty().size() ) ;		
	}
		
	public void updateLayers ( typeVectorField typeVectorField ,  int depthMax  , boolean createSeed , double minDistSeed   ) { 
	//	System.out.println("numberNodes "+ graph.getNodeCount() +"\n"+"numberSeeds "+ lSeed.getListSeeds().size());	
	
		Dijkstra dijkstra = new Dijkstra(Element.EDGE, "length", "length") ; 
		ArrayList<seed> listSeedsToRemove  = new ArrayList<seed> (); 
		 
		if (createSeed) 
			createSeed(minDistSeed);
		
		for ( seed s : lSeed.getListSeeds() ) {
			
			lSeed.setSeedInCell(s, false);			
			Node nodeS = s.getNode() , nodeF ;
			
			// get cooord of potential node
			idNode = Integer.toString(idNodeInt)  ;
			graph.addNode( idNode ) ; 
			nodeF = graph.getNode( idNode ) ;
			if ( isFeedBackModel)
				lNet.setNodeInCell(nodeF, true );
	
			// compute vector
			double[] vec = lSeed.getVector(s , typeVectorField, typeRadius );	
			double coordX = s.getX() + vec[0] , coordY =  s.getY() + vec[1] ;
			
			if ( layerSeed.handleLimitBehaviur.equals( handleLimitBehaviur.stopSimWhenReachLimit )  ) 
				if ( coordX < 2 || coordX >= lRd.getSizeGrid()[0] - 1 ||  coordY < 2 || coordY >= lRd.getSizeGrid()[1] - 1  ) {				
			//		listSeedsToRemove.addAll(lSeed.getListSeeds());
					seedHasReachLimit = true ;
					System.out.print("sim stoped because a seed reach world limit");					
				//	System.exit(0);
					break;
				}
			
			
			s.setCoords(coordX , coordY);	
			lSeed.setSeedInCell(s, true);
			
			// set coordinates of new node
			nodeF.setAttribute("xyz", coordX , coordY , 0);
			
			// create edge
			idEdge = Integer.toString(idEdgeInt+1 );
			Edge e = graph.addEdge(idEdge, nodeS , nodeF) ;
			e.addAttribute("length", getDistGeom(nodeS,nodeF)); 
			
			// link seed to node
			s.setNode(nodeF);
			idNodeInt++;
			idEdgeInt++;
			
			bks.putNode(nodeF);
				
			// compute length vector
			double inten = Math.min(0.1 , getDistGeom(nodeS, nodeF) ) ;
			if ( inten == 0 ) {
				listSeedsToRemove.add(s);
				continue ;
			}
				
			// get node in buckets near nodeF
			ArrayList <Node> nodeinRadius = bks.getNodesInRadius(nodeF, 1) ;
			if ( nodeinRadius == null ) {
				listSeedsToRemove.add(s) ;
				continue ;
			}
			ArrayList <Node> listNodeInBks = new ArrayList<Node>(nodeinRadius) ;	
			
			Node nodeNear = null ;
			
			// nearest node to nodeF
			for( Node n : listNodeInBks ) 
				if ( getDistGeom(n, nodeF ) < inten && !n.equals(nodeS) ) {
					nodeNear = n ;
					break ;	
				}	
	
			if( nodeNear != null  && nodeNear != nodeS ) {
				if ( depthMax == 0) {
					try {
						idEdge = Integer.toString(idEdgeInt+1 );
						Edge ed = graph.addEdge(idEdge, nodeNear , nodeF) ;
						ed.addAttribute("length", getDistGeom(nodeNear,nodeF));
						listSeedsToRemove.add(s);	
						idEdgeInt++;	 
					} catch (EdgeRejectedException ex) {				}
				}
				else {		
					dijkstra.setSource(nodeF);		
					dijkstra.init(graph);
					dijkstra.compute();
				try {
					idEdge = Integer.toString(idEdgeInt+1 );
					Edge ed = graph.addEdge(idEdge, nodeNear , nodeF) ;
					ed.addAttribute("length", getDistGeom(nodeNear,nodeF));	
					idEdgeInt++;	 
					int distTopo = dijkstra.getPath(nodeNear).size() - 2 ;
					if ( distTopo >= depthMax || distTopo == - 2 ) 
						listSeedsToRemove.add(s);								
				}
				 catch (EdgeRejectedException ex) {	 }			
				}
			}
		}
		if ( isFeedBackModel )
			listSeedsToRemove.stream().forEach(s-> lSeed.removeSeed(s , true ));
		else 
			listSeedsToRemove.stream().forEach(s-> lSeed.removeSeed(s));			
	}
	
	//create seed without remove cell check degree and seed neig
	private void createSeed (double minDistSeed) { 	
		for ( cell c : lMl.getListMaxLoc()) {	
			double[] coordC = new double[] { c.getX() , c.getY()} ;		
			Node nearest = getNearestNode(coordC, bks.getNodesInRadius(coordC, 2) ) ;	
			if ( nearest != null ) {
				int degree = nearest.getDegree() ;		
				if ( degree == 2 ) {
					double[] coordNearest = GraphPosLengthUtils.nodePosition(nearest) ;		
					if ( getDistGeom(coordNearest, coordC) < minDistSeed ) {
						lSeed.createSeed(coordNearest[0], coordNearest[1], nearest , isFeedBackModel );					
					}
				}
			}
		}
	}
			
// SETUP AND HANDLE LAYER ---------------------------------------------------------------------------------------------------------------------------
	// not used. Seed methods in layerSeed 
	private void createNetCircle () {	
		double[] centerLayerRd = lRd.getCenter () ;
		double 	centerX = centerLayerRd[0] , 
				centerY = centerLayerRd[1] ,
				angle = 2 * Math.PI / numNodes ;		
		for ( idNodeInt = 0 ; idNodeInt < numNodes ; idNodeInt++ ) {
			
			double 	coordX = radius * Math.cos( idNodeInt * angle ) ,
					coordY = radius * Math.sin( idNodeInt * angle ) ;
					
			idNode =  Integer.toString(idNodeInt) ;
			graph.addNode(idNode) ;
			Node n = graph.getNode(idNode) ;
			n.addAttribute("xyz", centerX + coordX ,  centerY + coordY , 0 );
			bks.putNode(n);
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
	}
	
	public Graph getGraph () {
		return graph;
	}
	
	public void display ( boolean display ) {
		if ( display)
			graph.display(false) ;
	}

// GET NODES ----------------------------------------------------------------------------------------------------------------------------------------
	// get nearest node
	private Node getNearestNode ( Node node , ArrayList<Node> listNodes ) {
		
		listNodes.remove(node);
		
		Node nearest = null;
		double dist = 10 ;	
		for ( Node n : listNodes) {
			double test = getDistGeom(n , node);
			if ( test < dist ) {
				nearest = n ;
				dist = test ;
			}
		}	
		return nearest ;
	}
	
	private Node getNearestNode ( double[] coord , ArrayList<Node> listNodes ) {		
		Node nearest = null;
		double dist = 10 ;	
		for ( Node n : listNodes) {
			double[] coordN = GraphPosLengthUtils.nodePosition(n);
			double test = getDistGeom(coord , coordN);
			if ( test < dist ) {
				nearest = n ;
				dist = test ;
			}
		}	
		return nearest ;
	}
	
	private ArrayList<Node> getListNodesInDepth(Node node ,int depthMax ) {
		
		ArrayList<Node> list = new ArrayList<Node> () ;
		Dijkstra dijkstra = new Dijkstra(Element.EDGE, "length", "length") ; 
		Iterator<? extends Node> iter = node.getBreadthFirstIterator() ; 
		
		dijkstra.setSource(node);
		dijkstra.init(graph);
		dijkstra.compute();
		int depth = 0 ;
		
		while ( iter.hasNext() && depth < depthMax  ) {
			Node next = iter.next();		
			ArrayList<Node> listPathNode = new ArrayList<Node> ((Collection<? extends Node>) dijkstra.getPath(next).getEachNode());
			for ( Node n : listPathNode)
				if ( ! list.contains(n))
					list.add(n) ;
			depth = listPathNode.size() - 1 ;
		}	
		list.remove(node) ;
		return list;
	}
		
// FFEDBACK -----------------------------------------------------------------------------------------------------------------------------------------
	public void setNodeInCell ( Node n , boolean hasNode) {
		cell c = lRd.getCell(n);
		c.setHasNode(hasNode);
	}
	
// METHODS TO HANDLE INTERACTIONS -------------------------------------------------------------------------------------------------------------------
	// get length edge
	public static double getLength ( Edge e ) {		
		double[] 	coordN0 = GraphPosLengthUtils.nodePosition(e.getNode0() ) , 
					coordN1 = GraphPosLengthUtils.nodePosition(e.getNode1() ) ; 
		return Math.pow( Math.pow(coordN0[0] - coordN1[0], 2) + Math.pow(coordN0[1] - coordN1[1], 2) , 0.5 ) ;	
	}
	
	private ArrayList<Edge> getListEdgewhitNodeInList ( ArrayList<Node> listNodes ){	
		ArrayList<Edge> listEdge = new ArrayList<Edge>  ( ) ;
		for (Node n : listNodes ) {
			for ( Edge e : n.getEdgeSet()) 
				if ( ! listEdge.contains(e))
					listEdge.add(e);
		}
		return listEdge;
	}
	
	private ArrayList<Edge> getListEdgeNeighbor ( Node node , double radius ) {
		
		ArrayList<Edge> listEdge = new ArrayList<Edge> ( ) ;	//	System.out.println(bks.getNodesInRadius( node, radius));
		for ( Node n : bks.getNodesInRadius( node, radius) ) 
			for ( Edge e : n.getEdgeSet() ) 
				if ( ! listEdge.contains(e))
					listEdge.add(e) ;
		
		return listEdge ;
	}
	
	private static ArrayList<Edge> getListEdgeXInList ( double[] coords0 , double[] coords1 , ArrayList<Edge> listEdgeInRadius ) {
		
		ArrayList<Edge> list = new ArrayList<Edge> () ;
		for ( Edge e : listEdgeInRadius ) {
			Node n0 = e.getNode0(),	n1 = e.getNode1();
			
			double [] 	n0Coord = GraphPosLengthUtils.nodePosition(n0) , 
						n1Coord = GraphPosLengthUtils.nodePosition(n1) ,
						
						intersectionCoord = getCoordIntersectionLine(coords0[0], coords0[1], coords1 [0], coords1 [1], n0Coord[0], n0Coord[1], n1Coord[0], n1Coord[1]) ;
				
			if ( intersectionCoord[0] >= Math.min(coords0[0], coords1[0]) && intersectionCoord[0] <=  Math.max(coords0[0], coords1[0]) )
				if ( intersectionCoord[1] >= Math.min(coords0[1], coords1[1]) && intersectionCoord[1] <=  Math.max(coords0[1], coords1[1]) )

				list.add(e) ; 			
		}
		return list ;
	}
	
	private static ArrayList<Edge> getListEdgeXInList ( Node nEdge0 , Node nEdge1, ArrayList<Edge> listEdgeInRadius ) {
		
		ArrayList<Edge> list = new ArrayList<Edge> () ;
		double[] 	coords0 = GraphPosLengthUtils.nodePosition(nEdge0),
					coords1 = GraphPosLengthUtils.nodePosition(nEdge1);
		
		for ( Edge e : listEdgeInRadius ) {
			Node n0 = e.getNode0(),	n1 = e.getNode1();
			double [] 	n0Coord = GraphPosLengthUtils.nodePosition(n0) , 
						n1Coord = GraphPosLengthUtils.nodePosition(n1) ,
						
						intersectionCoord = getCoordIntersectionLine(coords0[0], coords0[1], coords1 [0], coords1 [1], n0Coord[0], n0Coord[1], n1Coord[0], n1Coord[1]) ;
				
			if ( intersectionCoord[0] >= Math.min(coords0[0], coords1[0]) && intersectionCoord[0] <=  Math.max(coords0[0], coords1[0]) )
				if ( intersectionCoord[1] >= Math.min(coords0[1], coords1[1]) && intersectionCoord[1] <=  Math.max(coords0[1], coords1[1]) )

				list.add(e) ; 			
		}
		return list ;
	}
	
	private static ArrayList<Edge> getListEdgeXInList (  Edge edge , ArrayList<Edge> listEdgeInRadius ) {
		
		double [] 	coords0 = GraphPosLengthUtils.nodePosition(edge.getNode0()) , 
				coords1 = GraphPosLengthUtils.nodePosition(edge.getNode1()) ;
				
		ArrayList<Edge> list = new ArrayList<Edge> () ;
		for ( Edge e : listEdgeInRadius ) {
			Node n0 = e.getNode0(),	n1 = e.getNode1();
			
			double [] 	n0Coord = GraphPosLengthUtils.nodePosition(n0) , 
						n1Coord = GraphPosLengthUtils.nodePosition(n1) ,
						
						intersectionCoord = getCoordIntersectionLine(coords0[0], coords0[1], coords1 [0], coords1 [1], n0Coord[0], n0Coord[1], n1Coord[0], n1Coord[1]) ;
				
			if ( intersectionCoord[0] > Math.min(coords0[0], coords1[0]) && intersectionCoord[0] <  Math.max(coords0[0], coords1[0]) )
				if ( intersectionCoord[1] >= Math.min(coords0[1], coords1[1]) && intersectionCoord[1] <=  Math.max(coords0[1], coords1[1]) )

				list.add(e) ; 			
		}
		return list ;
	}
	
	public static double[] getCoordIntersectionLine ( double x1 , double y1 , double x2 , double y2 , double x3 , double y3 , double x4 , double y4  ) {
		double 	xi = 0 , yi =  0 ,
				m1 , m2 , q1 , q2 ;
		
		double[] coordInter = new double[2] ;
		
		m1 = ( y1 - y2 ) / ( x1 - x2 ) ;
		m2 = ( y3 - y4 ) / ( x3 - x4 ) ;
		
		q1 = y1 - m1 * x1 ;
		q2 = y3 - m2 * x3 ;
		
		xi = ( q2 - q1 ) / ( m1 - m2 ) ;
		yi = m1 * xi  + q1 ;
	
		
		coordInter[0] = xi ; coordInter [1] = yi ;
	//	System.out.println( m2 *xi + q2 );
		return coordInter ;
	}

	// method to return sorted map ( min -> max ) by values 
	public static Map getMapTopValues ( Map <Node , Double> map , int limit ) {
				
		
		return  map.entrySet().stream()
			       .sorted(Map.Entry.comparingByValue(Comparator.naturalOrder()))
			       .limit(limit)
			       .collect(Collectors.toMap(
			       Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}
	
	// method to return sorted map ( min -> max ) by values 
	public static Map<Node, Double> getMapNodeDist ( Node node, ArrayList<Node> listNode , int limit ) {
		Map<Node,Double> map = new HashMap() ;
		for ( Node n: listNode ) 
			map.put(n, getDistGeom(node, n));		
		
		return  map.entrySet().stream()
			       .sorted(Map.Entry.comparingByValue(Comparator.naturalOrder()))
			       .limit(limit)
			       .collect(Collectors.toMap(
			       Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}
	
	// get list of neighbor 
	public ArrayList<Node> getListNeighbors ( Node node ) { 
		
		ArrayList<Node> listNeig = new ArrayList<Node>();
		
		Iterator<Node> iter = node.getNeighborNodeIterator() ;	
		while (iter.hasNext()) {		 
			Node neig = iter.next() ;		//		System.out.println(neig.getId() + neig.getAttributeKeySet());
			if ( !listNeig.contains(neig) )
				listNeig.add(neig);
		} 
		listNeig.remove(node) ; 
		return listNeig ;
	}
	
	public Node[] getNeighbors ( Node node ) {
		Node[] arrayNeig = null ;
		Iterator<Node> iter = node.getNeighborNodeIterator() ;	
		int pos = 0 ;
		while (iter.hasNext()) {		 
			arrayNeig[pos] =  iter.next() ;
			pos++ ;
		}
		return arrayNeig;
	}
	
	// get list of neighbor 
	public ArrayList<Node> getListNeighbors ( Node node , boolean addNode) { 
		
		ArrayList<Node> listNeig = new ArrayList<Node>();
		
		Iterator<Node> iter = node.getNeighborNodeIterator() ;	
		while (iter.hasNext()) {		 
			Node neig = iter.next() ;		//		System.out.println(neig.getId() + neig.getAttributeKeySet());
			if ( !listNeig.contains(neig) )
				listNeig.add(neig);
		}
		if ( addNode == false )
			listNeig.remove(node) ;
		return listNeig ;
	}
	
}
