package RdmGridToNetMultiThread;

import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.GraphPosLengthUtils;

public abstract class framework {
	
	private static String idPattern;
//	protected static bucketSet bks = new bucketSet() ;
//	protected static layerCell lRd = new layerCell() ,
//			lBumps = new layerCell() ,
//			lParab = new layerCell();
//	
//	protected static layerMaxLoc lMl = new layerMaxLoc();
//	protected static layerSeed lSeed = new layerSeed() ;
//
//	protected static vectorField vfRd = new vectorField() ,
//			vfBumps = new vectorField( ),
//			vfParab = new vectorField ();
//	
//	protected static layerNet lNet = new layerNet() ;
	protected static symplifyNetwork simNet = new symplifyNetwork() ;
	public enum RdmType { holes , solitions , movingSpots , pulsatingSolitions , mazes , U_SkateWorld , f055_k062 , chaos , spotsAndLoops , worms , waves }
	

	
// GET METHODS --------------------------------------------------------------------------------------------------------------------------------------	
	// get spatial distance from 2 nodes 
	public static double getDistGeom ( Node n1 , Node n2 ) {	
		
		double [] 	coordN1 = GraphPosLengthUtils.nodePosition(n1) , 
					coordN2 = GraphPosLengthUtils.nodePosition(n2); 
		
		return  Math.pow(Math.pow( coordN1[0] - coordN2[0] , 2 ) + Math.pow( coordN1[1] - coordN2[1] , 2 ), 0.5 )  ;
	}
	
	public static double getDistGeom ( double [] coordN1 , double [] coordN2 ) {			
		return  Math.pow(Math.pow( coordN1[0] - coordN2[0] , 2 ) + Math.pow( coordN1[1] - coordN2[1] , 2 ), 0.5 )  ;
	}
	
//	public bucketSet getBks () {
//		return bks;
//	}
//	public layerCell getLRd ( ) {
//		return lRd ;
//	}
//	public layerCell getLBumbs ( ) {
//		return lBumps ;
//	}
//
	
	// initialize world with more than 1 circle and corresponding Rdm pulse 
	public static void initCircle ( double valA , double valB , int numNodes , int centreX ,int centreY , int radiusRd , int radiusNet ) {	
	//	lRd.setValueOfCellAround(valA, valB, centreX, centreY, radiusRd);		
	
	//	lSeed.initSeedCircle(numNodes, radiusNet, centreX,centreY );
	}
	
	// set RD start values to use in similtion ( gsAlgo )
		protected static double[] getRdType ( RdmType pattern ) {
			double f,k ;
			switch ( pattern ) {
				case holes: 				{ f = 0.039 ; k = 0.058 ; } 
											break ;
				case solitions :			{ f = 0.030 ; k = 0.062 ; } 
											break ; 
				case mazes : 				{ f = 0.029 ; k = 0.057 ; } 
											break ;
				case movingSpots :			{ f = 0.014 ; k = 0.054 ; } 
											break ;
				case pulsatingSolitions :	{ f = 0.025 ; k = 0.060 ; } 
											break ;
				case U_SkateWorld :			{ f = 0.062 ; k = 0.061 ; } 
											break ;
				case f055_k062 :			{ f = 0.055 ; k = 0.062 ; } 
											break ;
				case chaos :				{ f = 0.026 ; k = 0.051 ; } 
											break ;
				case spotsAndLoops :		{ f = 0.018 ; k = 0.051 ; } 
											break ;
				case worms :				{ f = 0.078 ; k = 0.061 ; } 
											break ;
				case waves :				{ f = 0.014 ; k = 0.045 ; } 
											break ;		
				default :
					f= 0 ; k = 0 ;
			}	
			idPattern = pattern.toString();	
			return new double [] {f,k};
		
		}
}
