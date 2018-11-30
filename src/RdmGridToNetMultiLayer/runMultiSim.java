package RdmGridToNetMultiLayer;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.graphstream.graph.Graph;
import RdmGridToNetMultiLayer.layerCell.typeDiffusion;

import RdmGridToNetMultiLayer.layerMaxLoc.typeComp;
import RdmGridToNetMultiLayer.layerMaxLoc.typeInit;
import RdmGridToNetMultiLayer.layerSeed.handleLimitBehaviur;
import RdmGridToNetMultiLayer.vectorField.typeRadius;
import RdmGridToNetMultiLayer.vectorField.typeVectorField;
import dataAnalysis.indicatorSet.indicator;
import dataAnalysis.analyzeNetwork;
import dataAnalysis.storeNetwork;

public class runMultiSim extends framework  {
	
	// common parameters
	private static int stepToStore = 10 , 
				stepToAnalyze = 10 ,
				stepToPrint = 100 ,
				stepMax = 5000 ;	

	// parameters multi sim 
	private static  double incremKill = 0.001 , 
			incremFeed = 0.001 ,
			minFeed = 0.001 ,
			maxFeed = 0.091 , 
			minKill = 0.001  ,
			maxKill = 0.091 ;
	
	private static  String  path = "/home/tiricom/test/" ;
		
	// store and analysis parameters 
	private static boolean  
			runStoreSimNet = false , 
			runStoreNet = false ,
			runSimNet = true , 
			runAnalysisNet =true ,
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
//	private static morphogen m = morphogen.b;
	private static double r = 2 ,
			minDistSeed = 1 , 
			alfa = 2 ;
//	private static typeVectorField tvf = typeVectorField.slopeDistanceRadius;
	
	// initialize circle seeds
	private static int numNodes = 50, 
			radiusRd = 2 , 
			radiusNet = 4 ;

	public static void main(String[] args) throws Exception {	
		run () ;
	}
	
	
	
	
	public static void run () throws Exception {
		System.out.println("size of grid x ,y : " +sizeGridX + " " + sizeGridY ); 
		System.out.println("step max , store , analysis : " + stepMax + " " + stepToStore + " " + stepToAnalyze );
		System.out.println("increm f , k : " +  incremFeed + " " + incremKill);
		System.out.println("min and max feed : " + minFeed + " " + maxFeed ) ;
		System.out.println("min and max kill : " + minKill + " " + maxKill );
		System.out.println("//----------------------------------------------------" + "\n");
	
		for ( double f = minFeed ; f <= maxFeed ; f = f + incremFeed ) 
			for ( double k = minKill ; k <= maxKill ; k = k + incremKill ) {
				
			// bucket set
			bks = new bucketSet(1, 1, sizeGridX, sizeGridY ); 
			bks.initializeBukets(); 
	
			// layer Rd
			lRd = new layerCell(1, 1, sizeGridX, sizeGridY ,2,5) ;
			lRd.initializeCostVal(new double[] {1,0});
			lRd.setValueOfCellAround(new double[] {1, 1}, sizeGridX/2,sizeGridY/2 ,3 );
			double [] fk = getRdType(RdmType.movingSpots);
	//		lRd.setGsParameters(fk[0] , fk[1] , 0.2, 0.1, typeDiffusion.mooreCost);
			lRd.setGsParameters(f , k , 0.2, 0.1, typeDiffusion.mooreCost);
			
			// layer max local
			lMl = new layerMaxLoc(true,lRd,true, typeInit.test, typeComp.wholeGrid ,1);
			lMl.initializeLayer();
			
			// layer bumps
			lBumps = new layerCell(1, 1, sizeGridX, sizeGridY ,3,3) ;
			lBumps.initCells();		
		//	lBumps.setGridInCoordsLayer(lBumps.getBumbsFromPosition (1 , 1 , 1));
			lBumps.setGridInValsLayer(lBumps.getBumbsFromPosition ( 1 , 1 , 10) , 0);		
			
			// layer infinite paraboloid 
			lParab = new layerCell(1, 1, sizeGridX, sizeGridY ,3,3) ;
			lParab.initCells();
			lParab.setGridInValsLayer(lParab.getInfiniteParaboloid(0, .2000, .200, new double[] {sizeGridX/2 ,sizeGridY/2 ,0} ), 0);
			
			// vector field Rd
			vfRd = new vectorField(lRd, 1, 1 , sizeGridX, sizeGridY, typeVectorField.slopeDistanceRadius) ;
			vfRd.setSlopeParameters( 1 , r, alfa, true, typeRadius.circle);
			
			vfBumps = new vectorField(lBumps,  1, 1 , sizeGridX, sizeGridY, typeVectorField.minVal);
			vfBumps.setMinDirectionParameters(0);
			
			vfParab = new vectorField(lParab, 1, 1, sizeGridX , sizeGridY, typeVectorField.interpolation);
			vfParab.setInterpolationParameters( 0, 1, 2);
	//		vfParab.setSlopeParameters( 0 , r, alfa, true, typeRadius.circle);
			
			lNet = new layerNet("net") ;
			
			// layer Seed
			lSeed = new layerSeed(handleLimitBehaviur.stopSimWhenReachLimit, new vectorField[] { vfRd , vfParab
																							 }
																							, new double[] { 1 , 0.0 }
			);
			lSeed.initSeedCircle(numNodes, radiusNet, sizeGridX/2, sizeGridY/2);	
			initCircle(perturVal0,perturVal1,numNodes , sizeGridX/2 ,sizeGridY/2, 2 , radiusNet );				
		//	lNet.getGraph().display(false) ;
			lNet.setLengthEdges("length" , true );
			
			Graph netGr = lNet.getGraph();
			
			NumberFormat nf = NumberFormat.getNumberInstance();
			nf.setMaximumFractionDigits(3);
			String nameFile = "f-"+ nf.format(f)+"_k-"+ nf.format(k)+"_";
			System.out.println(nameFile + (new Date().toString()));
	
			// Initialize simplify network
			simNet = new symplifyNetwork(runSimNet, netGr);
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
			
			while ( t <= stepMax //  && ! lSeed.getListSeeds().isEmpty() 
					&& lNet.seedHasReachLimit == false 
					) {	
				
				if ( t / (double) stepToPrint - (int)(t / (double) stepToPrint ) < 0.0001) 	
			
					System.out.print( t +", ");
									
				
				
				// update layers
				lRd.updateLayer();
				lMl.updateLayer();
				lNet.updateLayers( 0 ,true,1);
				
				// store network
				storeNet.storeDSGStep(t);
				storeSimNet.storeDSGStep(t);
				
				// simplify network 
				simNet.compute(t);

				// analysis network
				analNet.compute(t);
				analSimNet.compute(t);
					
				t++ ;
			}
			System.out.println("\n" + "step " + t + " seed " + lSeed.getListSeeds().size() + " node " + lNet.getGraph().getNodeCount()+ "\n");
		
			// close files
			storeNet.closeStore();
			storeSimNet.closeStore();
			
			analNet.closeFileWriter();
			analSimNet.closeFileWriter();
				
	
		
			
		}		
	}
	
}
