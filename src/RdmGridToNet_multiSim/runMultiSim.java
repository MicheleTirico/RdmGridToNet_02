package RdmGridToNet_multiSim;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import org.graphstream.graph.Graph;

import RdmGridToNet.framework.RdmType;
import RdmGridToNet_multiSim.framework.typeDiffusion;
import RdmGridToNet_multiSim.framework.typeRadius;
import dataAnalysis.analyzeNetwork;
import dataAnalysis.storeNetwork;
import dataAnalysis.storeRd;
import dataAnalysis.indicatorSet.indicator;
import dataAnalysis.storeRd.whichMorpToStore;
import netViz.handleVizStype;
import netViz.handleVizStype.stylesheet;
import viz.*;

public class runMultiSim extends framework {
	
// SETUP PARAMETERS ---------------------------------------------------------------------------------------------------------------------------------
	// common parameters
	private static int stepToStore = 10 , 
			stepToAnalyze = 10 ,
			stepToPrint = 100 ,
			stepMax = 200 ;
	 
	// parameters multi sim 
	private static  double incremKill = 0.001 , 
			incremFeed = 0.001 ,
			minFeed = 0.03 ,
			maxFeed = 0.033 , 
			minKill = 0.031  ,
			maxKill = 0.032 ;
	 
	private static  String  path = "D:\\ownCloud\\RdmGrid_exp\\test" ;
	
	// store and analysis parameters 
	private static boolean  runStoreRd = false ,
			runStoreSimNet = false  , 
			runStoreNet = false ,
			runSimNet = true , 
			runAnalysisNet = true ,
			runAnalysisSimNet = true ;
	
	// layer Rd
	private static int sizeGridX = 200, 
			sizeGridY = 200 ;
	private static double Da = 0.2 ,
			Db = 0.1 ,
			initVal0 = 1 ,
			initVal1 = 0 ,
			perturVal0 = 1 ,
			perturVal1 = 1 ;	
	
	private static typeDiffusion tyDif = typeDiffusion.mooreCost ;
	
	// layer Local Max
	
	// layer seed and vector field
	private static morphogen m = morphogen.b;
	private static double r = 2,
			minDistSeed = 1 ,
			alfa = 2 ;
	private static typeVectorField tvf = typeVectorField.slopeDistanceRadius;
	
	// initialize circle seeds
	private static int numNodes = 50, 
			radiusRd = 2 , 
			radiusNet = 4 ;


	static layerRd lRd = new layerRd(1, 1, 200, 200 , typeRadius.circle ) ;
	static layerRd local ;
	public static void main(String[] args) throws Exception {	
		
		System.out.println("step max , store , analysis : " + stepMax + " " + stepToStore + " " + stepToAnalyze );
		System.out.println("increm f , k : " +  incremFeed + " " + incremKill);
		System.out.println("min and max feed : " + minFeed + " " + maxFeed ) ;
		System.out.println("min and max kill : " + minKill + " " + maxKill );
		System.out.println("//----------------------------------------------------" + "\n");
		
		lRd.initializeCostVal(1,0);	
		lRd.setDiffusion(0.2, 0.1, typeDiffusion.mooreCost);
		lRd.getCell(100, 100).setVals(1, 1);
		
		
		
		for ( double f = minFeed ; f <= maxFeed ; f = f + incremFeed ) 
			for ( double k = minKill ; k <= maxKill ; k = k + incremKill ) {
				
				local = lRd ;
				System.out.println(local);
				local.setFeedAndKill(f,k);
				
				Viz viz = new Viz(local);
				int t = 0 ;
				while ( t < 200) {
					local.updateLayer();
					viz.step();
					 
					
				//	 System.out.println(t);
					 t++;
				 }
			}
		
	
			
	


	}
	



	
	
}