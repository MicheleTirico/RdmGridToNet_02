package RdmGridToNet;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.graphstream.graph.Graph;

import RdmGridToNet.framework.morphogen;
import RdmGridToNet.framework.typeVectorField;
import RdmGridToNet.layerMaxLoc.typeComp;
import RdmGridToNet.layerMaxLoc.typeInit;
import RdmGridToNet.layerRd.typeDiffusion;
import RdmGridToNet.layerSeed.handleLimitBehaviur;
import dataAnalysis.analyzeNetwork;
import dataAnalysis.storeNetwork;
import dataAnalysis.storeRd;
import dataAnalysis.indicatorSet.indicator;
import dataAnalysis.storeRd.whichMorpToStore;
import netViz.handleVizStype;
import netViz.handleVizStype.stylesheet;

public class runMultiSim_fistful extends framework {
	
// SETUP PARAMETERS ---------------------------------------------------------------------------------------------------------------------------------
	// common parameters
	private static int stepToStore = 10 , 
			stepToAnalyze = 10 ,
			stepToPrint = 100 ,
			stepMax = 2000 ;
	 
	// parameters multi sim 
	private static  double incremKill = 0.005 , 
			incremFeed = 0.005 ,
			minFeed = 0.045 ,
			maxFeed = 0.081 , 
			minKill = 0.005  ,
			maxKill = 0.081 ;
	 
	private static  String  path = "D:\\ownCloud\\RdmGrid_exp\\multiSim_grid100_fistful" ;
	
	// store and analysis parameters 
	private static boolean  runStoreRd = false ,
			runStoreSimNet = false  , 
			runStoreNet = false ,
			runSimNet = true , 
			runAnalysisNet = true ,
			runAnalysisSimNet = true ;
	
	// layer Rd
	private static int sizeGridX = 100, 
			sizeGridY = 100 ;
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
	
	public static void main(String[] args) throws Exception {	
		
		System.out.println("step max , store , analysis : " + stepMax + " " + stepToStore + " " + stepToAnalyze );
		System.out.println("increm f , k : " +  incremFeed + " " + incremKill);
		System.out.println("min and max feed : " + minFeed + " " + maxFeed ) ;
		System.out.println("min and max kill : " + minKill + " " + maxKill );
		System.out.println("//----------------------------------------------------" + "\n");
	
		for ( double f = minFeed ; f <= maxFeed ; f = f + incremFeed ) 
			for ( double k = minKill ; k <= maxKill ; k = k + incremKill ) {
		
	//			System.out.println(f+ " " + k );
				
				// bucket set
				bks = new bucketSet(1, 1, sizeGridX, sizeGridY );
				bks.initializeBukets();

				// layer Rd
				lRd = new layerRd(1, 1, sizeGridX, sizeGridY, typeRadius.circle);		
				lRd.initializeCostVal(initVal0 , initVal1 );	
//				lRd.initializeRandomVal(10, 20, 0, 0, 1, 1);
				lRd.setGsParameters(f, k, Da, Db, tyDif );
				
				// layer max local
				lMl = new layerMaxLoc(true,true, typeInit.test, typeComp.aroundNetGraph, m );
				lMl.initializeLayer();
				
				// layer net
				lNet = new layerNet("net") ;	
				lNet.setLengthEdges("length" , true );
				Graph netGr = lNet.getGraph();
				
				// layer seed
				lSeed = new layerSeed( r , morphogen.b , alfa , handleLimitBehaviur.stopSimWhenReachLimit );
		
				// initialize network and seed
				initMultiFistFul(100, 20, 20, 3, 50  , morphogen.b, 1);	
				
				NumberFormat nf = NumberFormat.getNumberInstance();
				nf.setMaximumFractionDigits(3);
				
				String nameFile = "f-"+ nf.format(f)+"_k-"+ nf.format(k)+"_";
				System.out.println(nameFile + (new Date().toString()));
				
				// initialize rd store csv
				storeRd storeRd = new storeRd(runStoreRd, whichMorpToStore.a, 10 , path, "storeRd", nameFile + "Rd") ;
				storeRd.initStore();
				
				// Initialize simplify network
				symplifyNetwork simNet = new symplifyNetwork(runSimNet, netGr);
				simNet.init( stepToAnalyze);
				Graph simNetGr = simNet.getGraph() ;
				
				// initialize store network
				storeNetwork storeNet = new storeNetwork(runStoreNet, stepToAnalyze, netGr, path, "storeNet", nameFile) ;
				storeNet.initStore();
						
				// initialize store simplified network 
				storeNetwork storeSimNet = new storeNetwork(runStoreSimNet, stepToAnalyze, simNetGr, path, "storeSimNet", nameFile);
				storeSimNet.initStore();
				
				// initialize analysis network
				analyzeNetwork analNet = new analyzeNetwork(runAnalysisNet, false ,stepToAnalyze, netGr, path, "analysisNet", nameFile);
				indicator.normalDegreeDistribution.setFrequencyParameters(10, 0, 10);
				indicator.degreeDistribution.setFrequencyParameters(10, 0, 10); 
				
				Map mapNet = new TreeMap<>();
				
				mapNet.put("sizeGrid",  sizeGridX);
				mapNet.put("Da", Da);
				mapNet.put("Db", Db);				
				mapNet.put("f", f);
				mapNet.put("k", k);
				mapNet.put("numStartSeed",  numNodes);
				mapNet.put("stepStore" , stepToStore) ;
				analNet.setupHeader(false, mapNet);
				
				analNet.setIndicators(Arrays.asList(
						indicator.seedCount ,
						indicator.degreeDistribution,
						indicator.totalEdgeLength,
						indicator.edgeCount ,
						indicator.totalEdgeLengthMST
						));
				analNet.initAnalysis();
				
				// initialize analysis simplify network
				analyzeNetwork analSimNet = new analyzeNetwork(runAnalysisSimNet, false ,stepToAnalyze, simNetGr, path, "analysisSimNet", nameFile);		
				indicator.normalDegreeDistribution.setFrequencyParameters(10, 0, 10);
				indicator.degreeDistribution.setFrequencyParameters(10, 0, 10); 
				indicator.pathLengthDistribution.setFrequencyParameters(100, 0, 10 );
				Map mapSimNet = new TreeMap<>();
				
				mapSimNet.put("sizeGrid",  sizeGridX);
				mapSimNet.put("Da", Da);
				mapSimNet.put("Db", Db);				
				mapSimNet.put("f", f);
				mapSimNet.put("k", k);
				mapSimNet.put("numStartSeed",  numNodes);
				mapSimNet.put("stepStore" , stepToStore) ;
			
				analSimNet.setupHeader(false, mapSimNet);
				
				analSimNet.setIndicators(Arrays.asList(
						indicator.degreeDistribution ,
						indicator.pathLengthDistribution ,			
						indicator.edgeCount
						));
				analSimNet.initAnalysis();
				
				int t = 0 ; 
				System.out.print("steps : " );
				lNet.seedHasReachLimit = false ;
				
				while ( t <= stepMax && ! lSeed.getListSeeds().isEmpty() && lNet.seedHasReachLimit == false   ) {	
					
					if ( t / (double) stepToPrint - (int)(t / (double) stepToPrint ) < 0.0001) {	
						System.out.print( t +", ");
					}					
					try { 
						// compute layers
						lRd.updateLayer(); 
						lMl.updateLayer();
						lNet.updateLayers(tvf , 0 , true , minDistSeed );

						// store network
						storeNet.storeDSGStep(t);
						storeSimNet.storeDSGStep(t);
						storeRd.storeStepRd(t);
						
						// simplify network 
						simNet.compute(t);

						// analysis network
						analNet.compute(t);
						analSimNet.compute(t);

						t++;
					} catch (OutOfMemoryError e) {
						e.printStackTrace();
						System.out.println("remove all files: " + nameFile);
						break ; 
					} 
				}

				System.out.println("\n" + "step " + t + " seed " + lSeed.getListSeeds().size() + " node " + netGr.getNodeCount()+ "\n");
				// close files
				storeNet.closeStore();
				storeSimNet.closeStore();
				storeRd.closeFileWriter();
				
				analNet.closeFileWriter();
				analSimNet.closeFileWriter();
				
				
				
			}
	


}
	

}