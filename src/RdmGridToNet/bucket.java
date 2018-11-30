package RdmGridToNet;

import java.util.ArrayList;

import org.graphstream.graph.Node;

public class bucket {

	private int X;
    private int Y;
    private double val1,val2 ;
    private ArrayList<Node> listNodes = new ArrayList<Node> () ;
    
    public bucket() {
        this(0,0,null);
    }        
    
    public bucket(int X, int Y ,ArrayList<Node> listNode) {
        this.X = X;
        this.Y = Y;
        this.listNodes = listNodes ;
    }
  
// GET METHODS -------------------------------------------------------------------------------------------------------------------------------------- 
    public int getX() {
        return X;
    }
    
    public int getY() {
        return Y;
    }
    
    public ArrayList<Node> getistNodes() {
    	return listNodes;
    }
   
// SET METHODS --------------------------------------------------------------------------------------------------------------------------------------
    public void setX(int X) {
        this.X = X;
    }
    public void setY(int Y) {
        this.Y = Y;
    }

    public void addNode ( Node n ) { 
    	listNodes.add(n);
    }
}