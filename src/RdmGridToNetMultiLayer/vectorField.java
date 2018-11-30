package RdmGridToNetMultiLayer;

import java.util.ArrayList;

import RdmGridToNetMultiLayer.layerCell.typeNeighbourhood;

public class vectorField extends framework {
	
	private double sizeX, sizeY;
	private int numVectorX, numVectorY , radius;
	private vector[][] vectors ;
	private layerCell lCell ;
	private typeVectorField typeVectorField ;
	private typeRadius typeRadius ;
	private int posVal ;
	private double exp  ;
	
	/**
	 * parameters for vectorField
	 */
	private double g , r , alfa ;
	private boolean ceckIntenVector ;
	
	protected enum typeVectorField { slopeDistanceRadius , gravitiy , minVal , maxVal , interpolation ,test  } 
	public enum typeRadius { square , circle}

	
	protected  ArrayList<vector> listVectors = new ArrayList<vector> ();
	
	public vectorField ( ) {
		this(null, 0,0,0 ,0 , null );	
	}

	public vectorField(layerCell lCell , double sizeX, double sizeY , int numVectorX, int numVectorY , typeVectorField typeVectorField) {
		this.lCell = lCell ;
		this.sizeX = sizeX ;
		this.sizeY = sizeY ;
		this.numVectorX = numVectorX ;
		this.numVectorY = numVectorY ;
		this.typeVectorField = typeVectorField ;
		vectors = new vector[numVectorX][numVectorY];
	}
	
	public void setParametersVectorField ( typeVectorField typeVectorField  , typeRadius typeRadius ) {
		this.typeVectorField = typeVectorField ;
		this.typeRadius = typeRadius ;
	}
	
	public void setSlopeParameters (int posVal , double r , double alfa , boolean ceckIntenVector , typeRadius typeRadius ) {
		this.posVal = posVal ; 
		this.r= r ;
		 this.alfa= alfa ;
		 this.ceckIntenVector = ceckIntenVector; 
		 this.typeRadius = typeRadius ;
	}
	
	public void setGravityParameters ( typeRadius typeRadius ) {
		this.typeRadius = typeRadius ;
	}
	public void setMinDirectionParameters (int posVal) {
		this.posVal = posVal ;
	}
	
	public void setInterpolationParameters (int posVal, int radius , double exp) {

		this.posVal = posVal ;
		this.radius = radius ;
		this.exp = exp ;
	}
	
	protected vector getVector ( seed s ) {
		vector v ; 
		switch (typeVectorField) {
		case slopeDistanceRadius:{
			double [] inten = getVectorSlopeDistanceRadius(s);
			v = new vector(s.getCoords(), inten, null) ;
			}break;

		case minVal :
			v = getVectorMinDelta (s ) ;
			break ;
		case interpolation :
			v = getVectorInterpolation (s , radius, exp );
			break ; 
		default:
			v = null ;
			break;
		}
		return v ;
	}
	
// Summarize vectors --------------------------------------------------------------------------------------------------------------------------------
	public static vector getvectorSum ( vector[] vectors , double[] increm ) {
		double[] intenSum = new double[3] ; 
		int pos = 0 ; 
		while (pos < vectors.length ) {
			double[] inten = vectors[pos].getInten();
			intenSum[0] = increm[pos] * ( intenSum[0] + inten[0] ) ;
			intenSum[1] = increm[pos] * ( intenSum[1] + inten[1] ) ;
			pos++;
		}
 		return new vector( null , intenSum , null ) ;
	}
	
// GET VECTOR ---------------------------------------------------------------------------------------------------------------------------------------
	private vector getVectorMinDelta (seed s ) {
		cell c = lCell.getCell(s);
		int[] posC = c.getPos();
		double val = c.getVals()[posVal];
		double vecX , vecY ;		
		double absDeltaSx = Math.abs(val - lCell.getCell(posC[0] - 1 , posC[1]) .getVals()[posVal]) ,
				absDeltaDx = Math.abs(val - lCell.getCell(posC[0] + 1 , posC[1]).getVals()[posVal] ) ,
				absDeltaTop = Math.abs(val -lCell.getCell(posC[0], posC[1] + 1 ).getVals()[posVal] ) ,
				absDeltaBot = Math.abs(val -lCell.getCell(posC[0], posC[1] - 1 ).getVals()[posVal] ) ;
		
		if ( absDeltaSx   > absDeltaDx )
			vecX = 1 / ( 1+ absDeltaDx ) ;
		else 
			vecX =  - 1 /  (1+  absDeltaSx  ) ;
		
		if ( absDeltaTop   > absDeltaBot )
			vecY = 1 /  ( 1+ absDeltaBot ) ;
		else 
			vecY = - 1 / ( 1+ absDeltaTop  ) ;

		return new vector( null , new double[] {vecX,vecY} , null ) ;
	}
	
	protected vector getVectorInterpolation ( seed s , int radius , double exp) {
		try {
			double vecX = 0 , vecY = 0 ;	
			double[] coordSeed  = s.getCoords();
			double nom = 0 ,
					denom = 0 ;				
			for ( int x = (int) Math.floor(coordSeed[0] - radius) ; x <= (int) Math.ceil(coordSeed[0]  + radius ) ; x++  )
				for ( int y = (int) Math.floor(coordSeed[1] - radius ) ; y <= (int) Math.ceil(coordSeed[1] + radius ) ; y++  ) {
					double dist = Math.pow(Math.pow(x - coordSeed[0], 2) + Math.pow(y - coordSeed[1], 2), 0.5);
					double val = lCell.getCell(x, y).getVals()[posVal];
					double denomInc = 1 / Math.pow(dist, exp) ,
							nomInc = val / Math.pow(dist, exp) ; 
					if (  Double.isInfinite(denomInc) )
						denomInc = 0 ;
					if ( Double.isInfinite(nomInc))
						nomInc = 0 ;
					denom = denom + denomInc ;
					nom = nom + nomInc  ; 
				}
			
			double diff = 100000 ;
			cell cellWin = null ;
			double valInter = nom / denom ;
			for ( int x = (int) Math.floor(coordSeed[0] - radius ) ; x <= (int) Math.ceil(coordSeed[0]  + radius ) ; x++  )
				for ( int y = (int) Math.floor(coordSeed[1] - radius ) ; y <= (int) Math.ceil(coordSeed[1] + radius ) ; y++  ) {
					cell c = lCell.getCell(x, y) ;
					double diffTest = Math.abs( valInter - c.getVals()[posVal] ) ;
					if ( diff > diffTest ) {
						diff = diffTest;
						cellWin = c ;
					}
			}
			vecX = cellWin.getX() - coordSeed[0] ;
			vecY = cellWin.getY() - coordSeed[1] ;
			return new vector( null , new double[] {vecX,vecY} , null ) ;
		} catch (NullPointerException e) {
			return null ;
		}
	}
	
	// get vector slope distance radius
	public double[] getVectorSlopeDistanceRadius ( seed s ) {
		double sX = s.getX() , sY = s.getY() , vecX = 0 , vecY = 0 ;
		for ( int x = (int) Math.floor(s.getX() - r ) ; x <= (int) Math.ceil(s.getX() + r ); x++ )
			for ( int y = (int) Math.floor(s.getY() - r ) ; y <= (int) Math.ceil(s.getY() + r ); y++ ) {	
				try {
					cell c = lCell.getCell(x,y);
					if ( typeRadius.equals(typeRadius.circle)) 
						if ( Math.pow(Math.pow(c.getX() - s.getX(), 2) + Math.pow(c.getY() - s.getY(), 2),0.5) > r ) 
							continue ;
				
					double 	distX = Math.pow(1+Math.abs(sY - y), alfa) ,
							distY = Math.pow(1+Math.abs(sX - x), alfa); 
			
					double 	addVecX = ( lCell.getCellVals(x+1, y)[posVal] - lCell.getCellVals(x-1, y)[posVal] ) / distY , 
							addVecY = ( lCell.getCellVals(x, y+1)[posVal] - lCell.getCellVals(x, y-1)[posVal] ) / distX ;
										
					vecX = vecX + addVecX ;
					vecY = vecY + addVecY ;		
					
					if ( Double.isNaN(vecX))			vecX = 0 ;
					if ( Double.isNaN(vecY))			vecY = 0 ;
				} catch (NullPointerException e) {
				}	
			}
		if ( ceckIntenVector ) {	
			vecX = checkValueVector(vecX, .1) ;
			vecY = checkValueVector(vecY, .1) ;
		}
		s.setVec( -vecX, -vecY);
		return new double[] {-vecX ,-vecY} ;
	}
	
	// check max value of vector 
		private double checkValueVector (double vec , double valMax) { 
			double 	vecAbs = Math.abs(vec) , 
					valMaxAbs = Math.abs(valMax);
			
			if ( vecAbs > valMaxAbs )
				if ( vec > 0.0 )
					return valMax;
				else 
					return -valMax ;
			else 
				return vec;
		}
	
}
