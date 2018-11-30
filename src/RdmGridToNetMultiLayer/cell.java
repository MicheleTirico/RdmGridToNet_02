package RdmGridToNetMultiLayer;

public class cell {
    private int X , Y ;
    private double[] arrayVals ,
    					coords = new double [3]; 
    private boolean[] arrayIsTest  ;
    
    public cell() {
    	this(0, 0, null, null, null);
    }        
    
    
    public cell( int  X, int Y , double[] coords,  double[] arrayVals , boolean[] arrayIsTest ) {
        this.X = X ;
        this.Y = Y ;
        this.coords = coords ;
        this.arrayVals = arrayVals ;
        this.arrayIsTest = arrayIsTest ;
    }
  
// GET METHODS -------------------------------------------------------------------------------------------------------------------------------------- 
    public int getX() {
        return X;
    }
    
    public int getY() {
        return Y;
    }
    
   public double[] getCoords() {
	   return coords ;
   }
    
    public double[] getVals ( ) {
    	return arrayVals ;
    }
    
    public boolean[] getArrayIsTest() {
    	return arrayIsTest ;
    }
    
    public int[] getPos ( ) {
    	return new int[] {X,Y} ;
    }
// SET METHODS --------------------------------------------------------------------------------------------------------------------------------------
    public void setX(int X) {
        this.X = X;
    }
    
    public void setY(int Y) {
        this.Y = Y;
    }
    
    public void setCoords( double[] coords ) {
    	this.coords = coords;
    }
    
    public void setVals ( double[] vals ) {
    	this.arrayVals = vals ;
    }
    
    public void setIsTest ( boolean[] isTest ) {
    	this.arrayIsTest = isTest ;
    }
    
    public void setVal ( int pos , double val ) {
    	arrayVals[pos] = val ;
    }
    
    
    
    public void setCoord ( int coord , double val ) {
    	coords[coord] = val ;
    }
    
    public void initCoords ( double sizeX , double sizeY  ) {
		setCoords(new double[] {getX()* sizeX , getY()*sizeY, 0});
	}
  
    public void setIsTest ( int pos , boolean val) {
    	if ( val )
    		arrayIsTest[pos] = true ;
    	else 
    		arrayIsTest[pos] = false ;
    }
}