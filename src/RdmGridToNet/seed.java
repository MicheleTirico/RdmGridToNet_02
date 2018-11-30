package RdmGridToNet;

import org.graphstream.graph.Node;

public class seed {
	private double 	X, Y, vecX, vecY;
	
	private Node n ;
	public seed() {
		this(0,0,0,0, null);
	}

	public seed(double X, double Y, double vecX, double vecY, Node n) {
		this.X = X ;
		this.Y = Y ;
		this.vecX = vecX ;
		this.vecY = vecY ;
		this.n = n ;
	}
	
// GET METHODS -------------------------------------------------------------------------------------------------------------------------------------- 
    public double getX() {
        return X;
    }
    
    public double getY() {
        return Y;
    }
    
    public double getVecX() {
    	return vecX;
    }
    
    public double getVecY() {
    	return vecY;
    }
    
    public Node getNode() {
    	return n;
    }
   
    public double getIntenVec ( ) {
    	return Math.pow(Math.pow(vecX, 2) + Math.pow(vecY, 2) , 0.5 ) ;
    }
    
// SET METHODS --------------------------------------------------------------------------------------------------------------------------------------
    public void setX(double X) {
        this.X = X;
    }
    
    public void setY(double Y) {
        this.Y = Y;
    }
  
    public void setCoords ( double X, double Y) {
    	this.X = X ;
    	this.Y = Y ;
    }
    
    public void setNode ( Node n ) {
    	this.n = n ;
    }
    
    public void setVecX ( double vecX ) {
    	this.vecX = vecX ;
    }
    
    public void setVecY ( double vecY ) {
    	this.vecY = vecY ;
    }
    
    public void setVec ( double vecX , double vecY ) {
    	this.vecY = vecY ;
    	this.vecX = vecX ;   	
    }
}
