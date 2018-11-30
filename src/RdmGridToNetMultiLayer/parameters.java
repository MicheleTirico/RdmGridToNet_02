package RdmGridToNetMultiLayer;

import RdmGridToNetMultiLayer.layerCell.typeDiffusion;

public class parameters extends framework {

	// common parameters
	static int stepToStore = 10 , 
				stepToAnalyze = 10 ,
				stepToPrint = 100 ,
				stepMax = 5000 ;	

	// parameters multi sim 
	public static  double incremKill = 0.001 , 
			incremFeed = 0.001 ,
			minFeed = 0.001 ,
			maxFeed = 0.091 , 
			minKill = 0.001  ,
			maxKill = 0.091 ;
	
	 static  String  path = "/home/tiricom/test/" ;
		
	// store and analysis parameters 
	 static boolean  
			runStoreSimNet = false , 
			runStoreNet = false ,
			runSimNet = true , 
			runAnalysisNet =true ,
			runAnalysisSimNet = true ;
	
	// layer Rd
	 static int sizeGridX = 100, 
			sizeGridY = 100 ;
	 static double Da = 0.2 , 
			Db = 0.1 ,
			initVal0 = 1 ,
			initVal1 = 0 ,
			perturVal0 = 1 ,
			perturVal1 = 1 ;	
	 static typeDiffusion tyDif = typeDiffusion.mooreCost ;
	
	// layer Local Max
	
	// layer seed and vector field
//	private static morphogen m = morphogen.b;
	 static double r = 2 ,
			minDistSeed = 1 , 
			alfa = 2 ;
//	private static typeVectorField tvf = typeVectorField.slopeDistanceRadius;
	
	// initialize circle seeds
	 static int numNodes = 50, 
			radiusRd = 2 , 
			radiusNet = 4 ;

}
