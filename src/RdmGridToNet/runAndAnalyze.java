package RdmGridToNet;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;

import RdmGridToNet.framework.RdmType;
import RdmGridToNet.framework.morphogen;
import RdmGridToNet.framework.typeVectorField;
import RdmGridToNet.layerMaxLoc.typeComp;
import RdmGridToNet.layerMaxLoc.typeInit;
import RdmGridToNet.layerRd.typeDiffusion;
import RdmGridToNet.layerSeed.handleLimitBehaviur;
import dataAnalysis.indicatorSet.indicator;
import dataAnalysis.analyzeNetwork;
import dataAnalysis.storeNetwork;
import dataAnalysis.storeRd;
import dataAnalysis.storeRd.whichMorpToStore;
import netViz.handleVizStype;
import netViz.handleVizStype.stylesheet;
import viz.*;

public class runAndAnalyze extends framework {
	// common parameters
		private static int stepToStore = 10 , 
				stepToAnalyze = 10 ,
				stepToPrint = 100 ,
				stepMax = 10000 ;
		
		private static  String  path = "D:\\ownCloud\\RdmGrid_exp\\test" ;
		
	// store and analysis parameters 
	private static boolean  runStoreRd = false ,
			runStoreSimNet = false , 
			runStoreNet = false ,
			runSimNet = false , 
			runAnalysisNet =false,
			runAnalysisSimNet = false;
	
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
	private static double r = 2 ,
			minDistSeed = 1 , 
			alfa = 2 ;
	private static typeVectorField tvf = typeVectorField.slopeDistanceRadius;
	
	// initialize circle seeds
	private static int numNodes = 50, 
			radiusRd = 2 , 
			radiusNet = 4 ;

	public static void main(String[] args) throws Exception {	
		// bucket set
		bks = new bucketSet(1, 1, sizeGridX, sizeGridY);
		bks.initializeBukets();

		// layer Rd
		lRd = new layerRd(1, 1, sizeGridX, sizeGridY, typeRadius.circle);		
		lRd.initializeCostVal(1,0);	
	//	lRd.initializeRandomVal(10, 100, 0.9, 0, 1, 0.1);
		
//		for ( cell c : lRd.getListCell() ) {			System.out.println(c.getVal1() + " " + c.getVal2() );}
		
		// set Rd classical pattern
		setRdType ( RdmType.solitions ) ;	
	//	f = 0.018 ; k = 0.051 ; 
		lRd.setGsParameters(f , k , Da, Db, typeDiffusion.mooreCost );
		
		lMl = new layerMaxLoc(true,true, typeInit.test, typeComp.wholeGrid, m );
		lMl.initializeLayer();
		
		lNet = new layerNet("net") ;	
		Graph netGr = lNet.getGraph();
		Graph locGr = lMl.getGraph() ;
		
		lSeed = new layerSeed( r , m , alfa ,handleLimitBehaviur.stopSimWhenReachLimit );
		initCircle(perturVal0,perturVal1,numNodes , sizeGridX/2 ,sizeGridY/2, 2 , radiusNet );		
		// lSeed.initializationSeedThMorp(m, 0.099 , 0.1) ;
//		initRandomPoint(10, 100, morphogen.b, 1);	
//		initfistfulOfNodes(100, 20, new double[] {  sizeGridX/2 ,sizeGridY/2}, 1 , morphogen.b, 1 );
//		initMultiFistFul(100, 20, 20, 2, 30  , morphogen.b, 1);
		
	//	initMultiCircle(20, 20, 20, 20 , 1, 0, 1);
		lNet.setLengthEdges("length" , true );

		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(3);
		
		String nameFile = "f-"+ nf.format(f)+"_k-"+ nf.format(k)+"_";
		System.out.println(nameFile);
		
		// initialize rd store csv
		storeRd storeRd = new storeRd(runStoreRd, whichMorpToStore.a, 10 , path, "storeRd", nameFile + "Rd") ;
		storeRd.initStore();
		
		// Initialize simplify network
		symplifyNetwork simNet = new symplifyNetwork(runSimNet, netGr);
		simNet.init( stepToAnalyze );
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
		analyzeNetwork analSimNet = new analyzeNetwork(runAnalysisSimNet, true ,stepToAnalyze, simNetGr, path, "analysisSimNet", nameFile);		
		indicator.normalDegreeDistribution.setFrequencyParameters(10, 0, 10);
		indicator.degreeDistribution.setFrequencyParameters(10, 0, 10); 
		indicator.pathLengthDistribution.setFrequencyParameters(10, 0, 5);
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
			//	indicator.totalEdgeLength ,
				indicator.edgeCount 
				));
		analSimNet.initAnalysis();

		// setup viz netGraph
		handleVizStype netViz = new handleVizStype( netGr ,stylesheet.manual , "seed", 1) ;
		netViz.setupIdViz(false , netGr, 20 , "black");
		netViz.setupDefaultParam (netGr, "black", "black", 5 , 0.5 );
		netViz.setupVizBooleanAtr(true, netGr, "black", "red" , false , false ) ;
		netViz.setupFixScaleManual( true , netGr, sizeGridX , 0);
		
		// setup viz netGraph
		handleVizStype simNetViz = new handleVizStype( simNetGr ,stylesheet.manual , "seed", 1) ;
		simNetViz .setupIdViz(false , simNetGr , 20 , "black");
		simNetViz .setupDefaultParam (simNetGr , "black", "black", 5 , 0.5 );
		simNetViz .setupVizBooleanAtr(true, simNetGr , "black", "red" , false , false ) ;
		simNetViz .setupFixScaleManual( false , simNetGr , sizeGridX , 0);

		netGr.display(false);	
	// 	simNetGr.display(false);
		// setup RD viz
		Viz viz = new Viz(lRd);

		int t = 0 ; 
		while ( t <= stepMax && ! lSeed.getListSeeds().isEmpty() && lNet.seedHasReachLimit == false ) {	
			System.out.println("---- step " +t +" --------------");
			System.out.println("numberNodes "+ netGr.getNodeCount() +"\n"+"numberSeeds "+ lSeed.getListSeeds().size());	
			System.out.println("numberMaxLo " + lMl.getNumMaxLoc());
			// compute layers
			lRd.updateLayer(); 
			lMl.updateLayer();
			lNet.updateLayers(typeVectorField.slopeDistanceRadius , 0 , true , 1 );

			// store network
			storeNet.storeDSGStep(t);
			storeSimNet.storeDSGStep(t);
			storeRd.storeStepRd(t);
			
			// simplify network 
			simNet.compute(t);
			
			// analysis network
			analNet.compute(t);
			analSimNet.compute(t);

			// RD viz
			viz.step();

			t++;
		}

		System.out.println(lSeed.getNumSeeds());
		// close files
		storeNet.closeStore();
		storeSimNet.closeStore();
		storeRd.closeFileWriter();
		
		analNet.closeFileWriter();
		analSimNet.closeFileWriter();
		
	//	simNet.compute();
	//	simNetGr.display(false);

		for ( Edge e : netGr.getEachEdge() ) {
			double len = e.getAttribute("length");
//			System.out.println(len);
		}
		
		// only for viz
		for ( seed s : lSeed.getListSeeds()) 	
			s.getNode().setAttribute("seed", 1);		
		
		
	}		
	public static int getGridSize() { return sizeGridX; }

	
}