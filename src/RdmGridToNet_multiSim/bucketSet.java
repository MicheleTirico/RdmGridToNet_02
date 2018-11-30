package RdmGridToNet_multiSim;

import java.util.ArrayList;

import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.GraphPosLengthUtils;

public class bucketSet extends framework {

	private bucket[][] buckets ;
	private double sizeX, sizeY;
	private int numBucketX, numBucketY;
	
	public bucketSet ( ) {
		this(0,0,0,0);
	}

	public bucketSet( double sizeX, double  sizeY, int numBucketX, int numBucketY) {
		this.sizeX = sizeX ;
		this.sizeY = sizeY ;
		this.numBucketX = numBucketX ;
		this.numBucketY = numBucketY;
		buckets = new bucket[numBucketX][numBucketY] ;
	}
	
// INITIALIZATION GRID ------------------------------------------------------------------------------------------------------------------------------
	public void initializeBukets (  ) {
		for (int x = 0; x < numBucketX ; x++)
			for (int y = 0; y < numBucketY ; y++) {
				bucket b = new bucket(x, y, new ArrayList<Node>());
				if ( !listBucket.contains(b)) {
					buckets[x][y] = b ;
					putBucketInList(b);			
				}
			}
	}

	public void putNode ( Node n ) {	
		double[] coords = GraphPosLengthUtils.nodePosition(n) ;
		bucket b = bks.getBucket(coords[0], coords[1]);
		b.addNode(n);
	}
	
// GET METHODS --------------------------------------------------------------------------------------------------------------------------------------	
	// get list nodes in bucket whick contains node with coords X Y
	private ArrayList<Node> getListNodes ( double X , double Y ) {
		return bks.getBucket(X, Y).getistNodes();
	}
	
	// get bucket contain coords X Y  
	private bucket getBucket (double X , double Y ) {
		try {
			double x =  sizeX * (int) ( X / sizeX );
			double y =  sizeY * (int) ( Y / sizeY ) ;		    	
		    return buckets[(int)x] [(int)y];     
		} catch (ArrayIndexOutOfBoundsException e) {
			return null ;
		}
		}

	// get list nodes in radius
	protected ArrayList<Node> getNodesInRadius ( Node n , double radius ) { 
		double[] coords = GraphPosLengthUtils.nodePosition(n) ;
		double X = coords[0] , Y = coords[1] ;
		ArrayList<Node> list = new ArrayList<Node>( );		
		for ( double x = X - radius ; x <= X + radius ; x = x + sizeX ) 
			for ( double y = Y - radius ; y <= Y + radius ; y = y + sizeY ) {
				try {
				for (Node no : getBucket(x, y).getistNodes()) 
					if (!list.contains(no) && getDistGeom(no, n) < radius )
						list.add(no);	
				} catch (NullPointerException e) {
					return null ;
				}
			}
		list.remove(n);
		return list;
	}
	
	// get list nodes in radius
	protected ArrayList<Node> getNodesInRadius ( double[] coords , double radius ) {		
		double X = coords[0] , Y = coords[1] ;
		ArrayList<Node> list = new ArrayList<Node>( );		
		for ( double x = X - radius ; x <= X + radius ; x = x + sizeX ) 
			for ( double y = Y - radius ; y <= Y + radius ; y = y + sizeY ) 	
				try {
					for (Node no : getBucket(x, y).getistNodes()) 
					
						if (!list.contains(no) && layerNet.getDistGeom(GraphPosLengthUtils.nodePosition(no), coords) < radius )
							list.add(no);	
					} catch (NullPointerException e) {
						// TODO: handle exception
					}
	//	list.remove(n);
		return list;
	}
	
	// get buckets around point x Y in radius
	private ArrayList<bucket> getBucketsInRadius (double X , double Y , double radius) {
		ArrayList<bucket> list = new ArrayList<bucket>( );		
		for ( double x = X - radius ; x <= X + radius ; x = x + sizeX ) 
			for ( double y = Y - radius ; y <= Y + radius ; y = y + sizeY ) 
				list.add(getBucket(x,y));
		list.remove(getBucket(X, Y));
		return list;
	}
	
	// get list of buckets not empty
	protected ArrayList<bucket> getListBucketNotEmpty() {		
		ArrayList<bucket> list = new ArrayList<bucket> ();
		for ( bucket b : listBucket ) 
			if( !b.getistNodes().isEmpty() )
				list.add(b);
		return list;
	}


// PRINT METHODS ------------------------------------------------------------------------------------------------------------------------------------
	protected void printListBucket () {
		for ( int x = 0 ; x < numBucketX ; x++ )
			for ( int y = 0 ; y < numBucketY ; y++ ) 
				System.out.println(x+" "+ y + buckets[x][y] ) ;				
	}
		
	protected void printNodeInBuchet ( ) {
		for ( int x = 0 ; x < numBucketX ; x++ )
			for ( int y = 0 ; y < numBucketY ; y++ ) {
				System.out.println(x+" "+ y );
				bucket b = buckets[x][y];
				if (!b.getistNodes().isEmpty())
					System.out.println(x+" "+ y + " " + b.getistNodes());
			}		
	}
	
// LIST BUCKETS ACTIVE ------------------------------------------------------------------------------------------------------------------------------
	private void putBucketInList (bucket b) {
		if ( !listBucket.contains(b))
			listBucket.add(b);
	}
 
	public ArrayList<bucket > getListBucket () {
		return listBucket ;		
	}
	
	public bucket getBucket (int X , int Y) {
		return  buckets[X][Y]; 
	}	
}
