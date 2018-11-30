package RdmGridToNet_multiSim;

public class cell extends framework {
    private int X;
    private int Y;
    private double val1,val2 ;
    private boolean isMaxLocal , hasNode , hasSeed ;
    
    public cell() {
        this(0,0,0,0 ,false);
    }        
    
    public cell(int X, int Y , double val1 , double val2 , boolean isMaxLocal ) {
        this.X = X;
        this.Y = Y;
        this.val1=val1;
        this.val2=val2;
        this.isMaxLocal = isMaxLocal ;
    }
    
    public cell(int X, int Y , double val1 , double val2 , boolean isMaxLocal, boolean hasNode , boolean hasSeed ) {
        this.X = X;
        this.Y = Y;
        this.val1=val1;
        this.val2=val2;
        this.isMaxLocal = isMaxLocal ;
        this.hasNode = hasNode ;
        this.hasSeed = hasSeed ;
    }
  
// GET METHODS -------------------------------------------------------------------------------------------------------------------------------------- 
    public int getX() {
        return X;
    }
    
    public int getY() {
        return Y;
    }
    
    public double getVal1() {
    	return val1;
    }
    
    public double getVal2() {
    	return val2;
    }
    
    public boolean isMaxLocal ( ) {
    	return isMaxLocal;
    }
   
    public boolean hasNode ( ) {
    	return hasNode;
    }
    
    public boolean hasSeed ( ) {
    	return hasSeed;
    }
// SET METHODS --------------------------------------------------------------------------------------------------------------------------------------
    public void setX(int X) {
        this.X = X;
    }
    
    public void setY(int Y) {
        this.Y = Y;
    }
    
    public void setVals(double val1, double val2) {
    	this.val1 = val1;
    	this.val2 = val2;
    }
    
    public void setMaxLocal ( boolean val) {
    	if ( val )
    		isMaxLocal = true ;
    	else 
    		isMaxLocal = false ;
    }
    
    public void setHasNode ( boolean val ) {
    	if ( val )
    		hasNode = true ;
    	else 
    		hasNode = false ;
    }
    
    public void setHasSeed ( boolean val ) {
    	if ( val )
    		hasSeed = true ;
    	else 
    		hasSeed = false ;
    }
}