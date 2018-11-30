package RdmGridToNet;

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

public class symplifyNetwork_old_01 extends framework{

	private Graph stSimGr = new SingleGraph ( "stSimNet" ),
					netGr = new SingleGraph ( "net" ) ,
					mulGr = new MultiGraph  ( "multiGr") ,
					grToCreate = null ;
	
	private int idNodeInt = 0 , idEdgeInt = 0 ;
	private String idNode , idEdge ; 
	public enum typeGraph { multiGraph, singleGraph };
	private typeGraph typeGraph ;
	private boolean run , addNode , addEdge;
	private double stepToAnalyze ;
	
	public symplifyNetwork_old_01() {
		this( false, null );
	}
	
	public symplifyNetwork_old_01( boolean run , Graph  netGr ) {
		this.run = run ;
		this.netGr = netGr ;
	}
	
	public void init(typeGraph typeGraph, boolean addNode , boolean addEdge) {
		this.typeGraph = typeGraph ;
		this.addNode = addNode ;
		this.addEdge = addEdge ;
		if ( typeGraph.equals(typeGraph.multiGraph) )
			grToCreate = mulGr ;
		else 
			grToCreate = stSimGr;
	}
	
	public void init(typeGraph typeGraph, boolean addNode , boolean addEdge, double stepToAnalyze) {
		this.typeGraph = typeGraph ;
		this.addNode = addNode ;
		this.addEdge = addEdge ;
		this.stepToAnalyze = stepToAnalyze ;
		if ( typeGraph.equals(typeGraph.multiGraph) )
			grToCreate = mulGr ;
		else 
			grToCreate = stSimGr;
	}
	
// COMPUTE METHODS ----------------------------------------------------------------------------------------------------------------------------------	
	public void compute ( ) {
		grToCreate.getEachNode().forEach(n -> grToCreate.removeNode(n));
		if ( run ) { 
			ArrayList < Node > listNodeAdded  = computeNodes(addNode, netGr, grToCreate);	
			computePaths(listNodeAdded, netGr, grToCreate, addEdge) ;
		}
	}

	public void compute ( int t ) {
		grToCreate.getEachNode().forEach(n -> grToCreate.removeNode(n));
		if ( run && t / stepToAnalyze - (int)(t / stepToAnalyze ) < 0.01 ) {  	
			ArrayList < Node > listNodeAdded  = computeNodes(addNode, netGr, grToCreate);	
			computePaths(listNodeAdded, netGr, grToCreate, addEdge) ;
		}
	}
	
	private ArrayList<Node> computeNodes ( boolean addNode , Graph grOr ,Graph grToCreate ) {
		ArrayList<Node> listNodeToAdd = new ArrayList<Node> () ;
		for ( Node nNet :grOr.getEachNode() ) {
			int d = nNet.getDegree();
			if ( d != 2 ) {
				listNodeToAdd.add(nNet);
				if ( addNode ) {
					Node nSim = createNode(grToCreate, nNet);
					nSim.addAttribute("nNet", nNet);
					nNet.addAttribute("nSim", nSim);
					nSim.addAttribute("dNet", d);
					nSim.addAttribute("listNeig", new ArrayList<Node> () );
					nSim.addAttribute("listPath", new ArrayList<Path> ());
					nSim.addAttribute("mapNeigLen", new HashMap<Node, Double>());
				}
			}
		}
		return listNodeToAdd ;
	}
	
	private void computePaths ( ArrayList < Node > listNodeAdded , Graph grOr ,Graph grToCreate , boolean addEdge ) {
		Dijkstra dijkstra = new Dijkstra(Element.EDGE, "length", "length") ; 
		dijkstra.init(grOr);		
		for ( Node n0Sim : grToCreate.getEachNode() ) {
			Node n0 = n0Sim.getAttribute("nNet"); 
			int dNo = n0.getDegree() ;
			dijkstra.setSource(n0);
			dijkstra.compute();
			for ( Node n1Sim : grToCreate.getEachNode() ) {
				if ( !n0Sim.equals(n1Sim)) {
					Node n1 = n1Sim.getAttribute("nNet");		
					Iterator< ? extends Path > it = dijkstra.getAllPathsIterator(n1);
					ArrayList<Path> listPath = new ArrayList<Path> () ;
					int numPath = 0 ;
					while ( it.hasNext() && numPath <= dNo) {
						Path next = it.next();
						listPath.add(next);
						numPath++;
					}
					for ( Path p : listPath ) {
						List<Node> nodePath = p.getNodePath();
						nodePath.remove(n0);
						nodePath.remove(n1);
						boolean goodPath = true ;
						int pos = 0 ;
						while ( goodPath == true && pos < nodePath.size() ) {
							Node n = nodePath.get(pos) ;
							if ( listNodeAdded.contains(n) )
								goodPath = false ;
							pos++ ;
						}
						if ( goodPath ) {		
							// add list of neighbors
							ArrayList<Node> listNeig = n0Sim.getAttribute("listNeig");
							if ( ! listNeig.contains(n1))
								listNeig.add(n1);
							n0Sim.addAttribute("listNeig", listNeig);
							
							// add list of paths from node to each neighbors
							ArrayList<Path> listPathNode = n0Sim.getAttribute("listPath");
							if ( ! listPathNode.contains(p))
								listPathNode.add(p);
							n0Sim.addAttribute("listPath", listPathNode);
							
							// add map neighbors - length				
							Map<Node, Double> mapNeigLen = n0Sim.getAttribute("mapNeigLen");
							if ( ! mapNeigLen.containsKey(n1))
								mapNeigLen.put(n1, dijkstra.getPathLength(n1));
							n0Sim.addAttribute("mapNeigLen", mapNeigLen);
											
							if ( addEdge) 
								if ( typeGraph.equals(mulGr))
									choiceGoodPathMultiGr(dijkstra, grToCreate, n0Sim, n1Sim, n1, p);
								else
									choiceGoodPathSingleGr(dijkstra, grToCreate, n0Sim, n1Sim, n1, p);
						}
					}
				}		
			}
		}
	}
	
	// create multiGraph through add edge 
	private void choiceGoodPathMultiGr ( Dijkstra dijkstra , Graph grToCreate , Node n0Sim , Node n1Sim , Node n1, Path p ) {
		Edge e = createEdge(grToCreate, n0Sim, n1Sim);
		if ( e!=null) {
			e.addAttribute("length", dijkstra.getPathLength(n1) );
			e.addAttribute("path", p);
		}
	}
	
	// create SingleGraph through add edge 
	private void choiceGoodPathSingleGr ( Dijkstra dijkstra , Graph grToCreate , Node n0Sim , Node n1Sim , Node n1, Path p ) {
		Edge ed = null ;
		double len = dijkstra.getPathLength(n1) ;
		Map<Path,Double>  mapPathLen = new HashMap<Path,Double>();
		Map<List<Node>,Double>  mapNodePathLen = new HashMap<List<Node> , Double>();
		ArrayList<Double> listLen = new ArrayList<Double>();
		
		ed = createEdge(grToCreate, n0Sim, n1Sim);	
		if ( ed == null ) {
			ed = getEdgeBetweenNodes(n0Sim, n1Sim);
			mapPathLen = ed.getAttribute("pathLen");
			mapNodePathLen = ed.getAttribute("nodePathLen");
			listLen = ed.getAttribute("listLen");
			if ( mapPathLen == null ) {
				mapPathLen = new HashMap<Path,Double>();
				mapNodePathLen = new HashMap<List<Node> , Double>(); 
				listLen = new ArrayList<Double> () ;
			}
		}
		else {
			mapPathLen = ed.getAttribute("pathLen");
			mapNodePathLen = ed.getAttribute("mapNodePathLen") ;
			listLen = ed.getAttribute("lisLen"); 
			if ( mapPathLen == null )
				mapPathLen = new HashMap<Path,Double>();
			if ( mapNodePathLen == null )
				mapNodePathLen = new HashMap<List<Node>,Double>();
			if ( listLen == null )
				listLen = new ArrayList<Double>();
		}
		if ( ! mapPathLen.containsValue(len)) {
			mapPathLen.put(p, len);
			ed.addAttribute("pathLen", mapPathLen) ;
			listLen.add(len);
			ed.addAttribute("listLen", listLen);
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
		return grToCreate;
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
	
	// create Node
	private Node createNode ( Graph gr , Node n ) {		
		double[] coord = GraphPosLengthUtils.nodePosition(n);	
		idNode = Integer.toString(idNodeInt); 
		Node newNode = gr.addNode(idNode);
		newNode.addAttribute("xyz", coord[0],coord[1] ,0);	
		idNodeInt++ ;
		return newNode ;
	}

}
