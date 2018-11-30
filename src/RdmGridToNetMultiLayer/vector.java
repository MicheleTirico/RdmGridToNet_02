package RdmGridToNetMultiLayer;

public class vector {

    private double[] coords = new double [3],
    		inten = new double[3] ; 
    
    private boolean[] arrayIsTest  ;
    
    public vector() {
    	this( null, null, null);
    }        

    public vector( double[] coords,  double[] inten , boolean[] arrayIsTest ) {   
        this.coords = coords ;
        this.inten = inten ;
        this.arrayIsTest = arrayIsTest ;
    }
    public vector(  double[] inten , boolean[] arrayIsTest ) {   
        this.inten = inten ;
        this.arrayIsTest = arrayIsTest ;
    }
    
// GET METHODS --------------------------------------------------------------------------------------------------------------------------------------   
   public double[] getCoords() {
	   return coords ;
   }
    
    public double[] getInten ( ) {
    	return inten ;
    }
    
    public boolean[] getArrayIsTest() {
    	return arrayIsTest ;
    }
    
// SET METHODS --------------------------------------------------------------------------------------------------------------------------------------
    
    public void setCoords( double[] coords ) {
    	this.coords = coords;
    }
    public void setSingleCoord ( int pos , double val ) {
    	coords[pos] = val ;
    }
    
    public void setInten ( double[] inten ) {
    	this.inten = inten ;
    }
    
    public void setSingleInten ( int pos , double val ) {
    	inten[pos] = val ;
    }
    
    public void setIsTest ( boolean[] isTest ) {
    	this.arrayIsTest = isTest ;
    }
    public void setSingleIsTest ( int pos , boolean val ) {
    	arrayIsTest[pos] = val ;
    }
}