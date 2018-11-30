package RdmGridToNet_multiSim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.graphstream.algorithm.APSP;
import org.graphstream.algorithm.APSP.APSPInfo;
import org.graphstream.algorithm.Dijkstra;
import org.graphstream.algorithm.Dijkstra.Element;
import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.graphicGraph.GraphPosLengthUtils;

import graphstream_dev_io_test.graphstreamReadGraph;
import netViz.netVizTest;

public class symplifyNetwork extends framework{

	private Graph simGr = new SingleGraph ( "simGr" ),
				  netGr = new SingleGraph ( "netGr" ) ;

	private int idNodeInt = 0 , idEdgeInt = 0 ;
	private String idNode , idEdge ; 
	private boolean run ;
	private double stepToAnalyze ;
	
	public symplifyNetwork() {
		this( false, null );
	}
	
	public symplifyNetwork( boolean run , Graph  netGr ) {
		this.run = run ;
		this.netGr = netGr ;
	}
	
	public void init( double stepToAnalyze ) {
		this.stepToAnalyze = stepToAnalyze  ;
	}
	
// COMPUTE METHODS ----------------------------------------------------------------------------------------------------------------------------------		
	public void compute_addArrayLen ( ) {
		if ( run ) {	
			for ( Node nNet : netGr.getEachNode()) {
				Node nSim = createNode(simGr, nNet);
				nSim.addAttribute("nNet", nNet);
				nNet.setAttribute("nSim", nSim);
			}
			for ( Edge eNet : netGr.getEachEdge()) {
				Node n0 = eNet.getNode0() , n1 = eNet.getNode1();
				Edge e = createEdge(simGr, n0.getAttribute("nSim"), n1.getAttribute("nSim") ) ;
				double[] lens = new double[2] ;
				lens[0] = getDistGeom(n0, n1);
				e.addAttribute("lens", lens);
			}
			for ( Node nNet : netGr.getEachNode() ) {
				Node nSim = nNet.getAttribute("nSim") ;
				int d = nSim.getDegree() ;
				if ( d == 2 ) {
					Node[] arrNeigs = getArrayNeighbors(nSim) ;
					Node n0 = arrNeigs[0],
							n1 = arrNeigs[1];
					
					double len0 = getDistGeom(nSim, n0),
							len1 = getDistGeom(nSim, n1);
					
					Edge e = createEdge(simGr, n0 , n1 , len0 + len1 ,  true ) ;		
					simGr.removeNode(nSim);
				}
			}	
		}
	}
	
	public void compute (  ) {
		if ( run ) {	
			for ( Node nNet : netGr.getEachNode()) {
				Node nSim = createNode(simGr, nNet);
				nSim.addAttribute("nNet", nNet);
				nNet.setAttribute("nSim", nSim);
			}
			for ( Edge eNet : netGr.getEachEdge()) {
				Node n0 = eNet.getNode0() , n1 = eNet.getNode1();
				Edge e = createEdge(simGr, n0.getAttribute("nSim"), n1.getAttribute("nSim") ) ;
				try {
					ArrayList<Double> listLen = new ArrayList<Double>(Arrays.asList(getDistGeom(n0, n1))) ;
					e.addAttribute("listLen", listLen);
				} catch ( OutOfMemoryError ex ) {
					e.addAttribute("listLen", new ArrayList<Double> ( Arrays.asList(0.0)));
				}
			}
			for ( Node nNet : netGr.getEachNode() ) {
				Node nSim = nNet.getAttribute("nSim") ;
				if ( nSim.getDegree() == 2 ) { 
					Node[] arrNeigs = getArrayNeighbors(nSim) ;
					Node n0 = arrNeigs[0],
							n1 = arrNeigs[1];
					
					double len = getDistGeom(nSim, n0) + getDistGeom(nSim, n1);
					
					Edge e = null ;
					try {
						idEdge = Integer.toString(idEdgeInt);
						e = simGr.addEdge(idEdge, n0, n1);
						ArrayList<Double> listLen = new ArrayList<Double>( Arrays.asList(len)) ;
						e.addAttribute("listLen", listLen);
						idEdgeInt++;
					} catch (EdgeRejectedException ex) {
						e = getEdgeBetweenNodes(n0, n1);
						ArrayList<Double> listLen = e.getAttribute("listLen");
						listLen.add(getDistGeom(n0, n1));
						e.addAttribute("listLen", listLen);
					}	
					simGr.removeNode(nSim);
				}
			}	
		}
	}
	
	public void compute ( int t ) {	

		simGr.getEachNode().forEach(n -> simGr.removeNode(n));
		if ( run && t / stepToAnalyze - (int)(t / stepToAnalyze ) < 0.01 ) {  			
			compute () ;
		}
	}
	

	
// GET METHODS --------------------------------------------------------------------------------------------------------------------------------------
	private ArrayList<Path> getListPath ( Graph gr ) {
		ArrayList<Path> list = new ArrayList<Path> () ;
		for ( Edge e : gr.getEachEdge()) {
			Path path = e.getAttribute("path") ;
			if ( ! list.contains(path))
				list.add(path);
		}
		return list;
	}
	
	private Map<Path,Edge> getMapEdgePath ( Graph gr ) {	
		Map<Path,Edge> map = new  HashMap<Path,Edge> () ;
		ArrayList<Path> list = new ArrayList<Path> () ;
		for ( Edge e : gr.getEachEdge()) {
			Path path = e.getAttribute("path") ;
			if ( ! list.contains(path)) {
				list.add(path);
				map.put( path , e );
			}
		}
		return map;
	}
	
	private Edge getEdgeBetweenNodes ( Node n0 , Node n1 ) {
		ArrayList<Edge> list = new ArrayList<Edge> ( ) ;
		for (Edge e : n0.getEdgeSet() ) 
			if ( e.getNode0().equals(n1) || e.getNode1().equals(n1) )
				return e ;
		return null ;
	}
	
	private ArrayList<Edge> getAllEdgeBetweenNodes ( Node n0 , Node n1 ) {
		ArrayList<Edge> list = new ArrayList<Edge> ( ) ;
		for (Edge e : n0.getEdgeSet() ) 
			if ( e.getNode0().equals(n1) || e.getNode1().equals(n1) )
				if ( ! list.contains(e) )
					list.add(e);	
		return list ;
	}
	
	private ArrayList<Node> getListNeighbors ( Node node ) { 
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
	
	private Node[] getArrayNeighbors ( Node node ) { 
		Node[] arr = new Node[node.getDegree()];
		Iterator<Node> iter = node.getNeighborNodeIterator() ;	
		int pos = 0 ;
		while (iter.hasNext()) {		 
			arr[pos] = iter.next() ; 
			pos ++ ;
		} 
		return arr ;
	}
	

	public Node getNearestNodeInPath (Graph gr , Node source ) {
		Iterator<? extends Node> k = source.getDepthFirstIterator();
		while ( k.hasNext() ) {	
			Node n =  k.next();
			int degree = n.getDegree();	
			if ( degree > 2) 
				return n; 		
		}
		return null ;
	}
	
	public ArrayList<Node> getListExt ( Node node ) {
		ArrayList<Node> listExt = new ArrayList<Node> () ;	
		Iterator<? extends Node> k = node.getDepthFirstIterator();
		int num = 0 ;
		while ( k.hasNext() && num < 2 ) {
			Node next = k.next();
			int dNext = next.getDegree();			
			if ( dNext !=2  && ! listExt.contains(next) ) {
				listExt.add(next);	
				num++ ;
			}	
		}
		return listExt ;
	}
	
	// get length edge 
	private double getLength ( Edge e ) {
		return e.getAttribute("length") ;
	}
	
	// get Graph
	public Graph getGraph ( ) {
		return simGr;
	}
	
	
	public void addIfNotContain ( ArrayList list , Object obj ) {
		if ( !list.contains(obj))
			list.add(obj);
	}
	

	
// CREATE METHODS -----------------------------------------------------------------------------------------------------------------------------------
	// create edge
	private Edge createEdge ( Graph gr ,Node n0 , Node n1 ) {		
		try {
			idEdge = Integer.toString(idEdgeInt);
			Edge e = gr.addEdge(idEdge, n0, n1);
			idEdgeInt++;
			return e ;
		} catch (EdgeRejectedException e) {

			return null ;
		}
	}
	
	private Edge createEdge ( Graph gr ,Node n0 , Node n1 , double len0 , boolean setLen) {		
		Edge e = null ;
		try {
			idEdge = Integer.toString(idEdgeInt);
			e = gr.addEdge(idEdge, n0, n1);
			double [] lens = new double[2];
			lens[0] = len0;
			e.addAttribute("lens", lens);
			idEdgeInt++;
		} catch (EdgeRejectedException ex) {
			e = getEdgeBetweenNodes(n0, n1);
			double [] lens = e.getAttribute("lens");
			lens[1] = getDistGeom(n0, n1);
			e.addAttribute("lens", lens);
		}
		return e ;
	}
	
	
	// create Node
	private Node createNode ( Graph gr , Node n ) {		
		double[] coord = GraphPosLengthUtils.nodePosition(n);	
		idNode = Integer.toString(idNodeInt); 
		Node newNode = gr.addNode(idNode);
		newNode.addAttribute("xyz", coord[0],coord[1] ,0);	
		idNodeInt++ ;
		return newNode ;
	}
	
	private Node createNode ( Graph gr , Node n , ArrayList<Node> nodesAlreadyExist ) {		
		if ( ! nodesAlreadyExist.contains(n) ) {
			double[] coord = GraphPosLengthUtils.nodePosition(n);	
			idNode = Integer.toString(idNodeInt); 
			Node newNode = gr.addNode(idNode);
			newNode.addAttribute("xyz", coord[0],coord[1] ,0);	
			newNode.addAttribute("nodeNetGr", n);
			n.addAttribute("nodeSimGr", newNode);
			idNodeInt++ ;
			return newNode ;
		}
		else 
			return n.getAttribute("nodeSimGr");
		
	}
	
	public void setLengthEdges ( String attr , boolean setLengthEdges) {
		if ( setLengthEdges ) 
			for ( Edge e : simGr.getEachEdge() ) 
				e.addAttribute("length", getLength(e));	
	}

}
