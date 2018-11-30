package RdmGridToNetMultiThread;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import org.graphstream.graph.Graph;

import RdmGridToNetMultiThread.layerCell.typeDiffusion;
import RdmGridToNetMultiThread.layerMaxLoc.typeComp;
import RdmGridToNetMultiThread.layerMaxLoc.typeInit;
import RdmGridToNetMultiThread.layerSeed.handleLimitBehaviur;
import RdmGridToNetMultiThread.vectorField.typeRadius;
import RdmGridToNetMultiThread.vectorField.typeVectorField;
import dataAnalysis.analyzeNetwork;
import dataAnalysis.indicatorSet.indicator;
import dataAnalysis.storeInfo;
import layerViz.vizLayerCell;

public class multiThread extends Thread {

	private int th ; 
	public multiThread(int th) {
		this.th = th;
	}
	
	static int numThread = 9 ;
	
	// common parameters
		private static int stepToStore = 10 , 
					stepToAnalyze = 10 ,
					stepToPrint = 1000 ,
					stepMax = 5000 ;	

		// parameters multi sim 
		private static  double incremKill = 0.001 , 
				incremFeed = 0.001 ,
				minFeed = 0.005 ,
				maxFeed = 0.095 , 
				minKill = 0.005  ,
				maxKill = 0.095 ;
		private static int numberOfSimulations =  (int) ((((maxFeed - minFeed ) / incremFeed ) + 1 ) * ( ( (maxKill - minKill ) / incremKill ) + 1 ) )  ;  ;
			
		//  "/home/researcher/multiSim/results_07/"
		private static  String  path = "/home/researcher/multiSim/results_08/" ;
			
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
		private static boolean ceckReachBord = false ;
		private static double deltaCech = 0.05 ;
		
		// layer Local Max
		
		// layer seed and vector field
//		private static morphogen m = morphogen.b;
		private static double r = 2 ,
				minDistSeed = 1 , 
				alfa = 2 ;
//		private static typeVectorField tvf = typeVectorField.slopeDistanceRadius;
	
		// initialize circle seeds
		private static int numNodes = 50 , 
				radiusRd = 2 , 
				radiusNet = 4 ;
		
	private static int numVfs ;
	private double [] arrayF = getArrayVals(minFeed, maxFeed, incremFeed);
	private double [] arrayK = getArrayVals(minKill , maxKill , incremKill );
	private ArrayList<double[]> params = getListParams(true , arrayF, arrayK);
	
	public void run (  ) {
	
		ArrayList<double[]> listParams = getParamsForThread(params,numThread, th);		//	System.out.println(th + " " + listParams);
		numberOfSimulations = listParams.size();
		System.out.println("start " + new Date().toString() + " numSim: " + listParams.size() );
		for ( double[] fk : listParams) {
			double f = fk[0], 
					k = fk[1];	//	System.out.println(f + " " + k);
			
			NumberFormat nf = NumberFormat.getNumberInstance();
			nf.setMaximumFractionDigits(3);
			String nameFile = "f-"+ nf.format(f)+"_k-"+ nf.format(k)+"_";
			
			System.out.println("start " + nameFile + " " + new Date().toString() );
			
			// bucket set
			bucketSet bks = new bucketSet(1, 1, sizeGridX, sizeGridY ); 
			bks.initializeBukets(); 
	
			// layer Rd
			layerCell lRd = new layerCell(1, 1, sizeGridX, sizeGridY ,2,5) ;
			lRd.initializeCostVal(new double[] {1,0});
			lRd.setValueOfCellAround(new double[] {1, 1}, sizeGridX/2,sizeGridY/2 ,3 );

			lRd.setReachBord( true , deltaCech);
	//		lRd.setGsParameters(fk[0] , fk[1] , 0.2, 0.1, typeDiffusion.mooreCost);
			lRd.setGsParameters(f , k , 0.2, 0.1, typeDiffusion.mooreCost);
			
			// layer max local
			layerMaxLoc lMl = new layerMaxLoc(true,true, typeInit.test, typeComp.wholeGrid ,1);
			lMl.setLayers(lRd, bks);
			lMl.initializeLayer();
			
			// layer bumps
			layerCell lBumps = new layerCell(1, 1, sizeGridX, sizeGridY ,3,3) ;
			lBumps.initCells();		
		//	lBumps.setGridInCoordsLayer(lBumps.getBumbsFromPosition (1 , 1 , 1));
			lBumps.setGridInValsLayer(lBumps.getBumbsFromPosition ( 1 , 1 , 10) , 0);		
			
//			// layer infinite paraboloid 
//			layerCell lParab = new layerCell(1, 1, sizeGridX, sizeGridY ,3,3) ;
//			lParab.initCells();
//			lParab.setGridInValsLayer(lParab.getInfiniteParaboloid(0, .2000, .200, new double[] {sizeGridX/2 ,sizeGridY/2 ,0} ), 0);
			
			// vector field Rd
			vectorField vfRd = new vectorField(lRd, 1, 1 , sizeGridX, sizeGridY, typeVectorField.slopeDistanceRadius) ;
			vfRd.setSlopeParameters( 1 , r, alfa, true, typeRadius.circle);
			
//			vectorField vfBumps = new vectorField(lBumps,  1, 1 , sizeGridX, sizeGridY, typeVectorField.minVal);
//			vfBumps.setMinDirectionParameters(0);
//			
//			vectorField vfParab = new vectorField(lParab, 1, 1, sizeGridX , sizeGridY, typeVectorField.interpolation);
//			vfParab.setInterpolationParameters( 0, 1, 2);
//			vfParab.setSlopeParameters( 0 , r, alfa, true, typeRadius.circle);
					
			// layer Seed
			vectorField[] vfs = new vectorField[] { vfRd } ; 
			numVfs = vfs.length ;
			layerSeed lSeed = new layerSeed(handleLimitBehaviur.stopSimWhenReachLimit, vfs
																							, new double[] { 1 //, 0.0
																									} );
																					
			// layer net
			layerNet lNet = new layerNet("net") ;
			lNet.setLayers( bks, lSeed, lRd, lMl);
			lSeed.setLayers(lNet, bks, lRd);
			lSeed.initSeedCircle(numNodes, radiusNet, sizeGridX/2, sizeGridY/2);	
			framework.initCircle(perturVal0,perturVal1,numNodes , sizeGridX/2 ,sizeGridY/2, 2 , radiusNet );				
	
			lNet.setLengthEdges("length" , true );
			
			Graph netGr = lNet.getGraph();
			
			// Initialize simplify network
			symplifyNetwork simNet = new symplifyNetwork(runSimNet, netGr);
			simNet.init( stepToAnalyze);
			Graph simNetGr = simNet.getGraph() ;

			analyzeNetwork analNet = null ,  
					analSimNet = null ;
			try {
				analNet = new analyzeNetwork(runAnalysisNet, false ,stepToAnalyze, netGr, path, "analysisNet", nameFile);
				indicator.normalDegreeDistribution.setFrequencyParameters(10, 0, 10);
				indicator.degreeDistribution.setFrequencyParameters(10, 0, 10); 
				analNet.setLayer(lSeed);
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
				analSimNet = new analyzeNetwork(runAnalysisSimNet, false ,stepToAnalyze, simNetGr, path, "analysisSimNet", nameFile);		
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

			}
			catch (IOException e) {
				// TODO: handle exception
			}
			
			
			int t = 0 ; 	//		System.out.print("steps : " );
			lNet.seedHasReachLimit = false ;	
			
			// viz layers
		//	vizLayerCell lRdViz = new vizLayerCell(lRd, 1);
		//	netGr.display(false) ;
//			simNetGr.display(false) ;
	//		lRd.getHasReachBord();
			
			
			while ( t <= stepMax // 	&& ! lSeed.getListSeeds().isEmpty() 
					&& lNet.seedHasReachLimit == false 
					&& lRd.getHasReachBord() == false
					) {	// 
		//		if ( t / (double) stepToPrint - (int)(t / (double) stepToPrint ) < 0.0001) System.out.println( nameFile +" " + "step: " + t);
	
				// update layers
				lRd.updateLayer();
				lMl.updateLayer();
				lNet.updateLayers( 0 ,true,1);
				
			//	lRdViz.step();
				
				// analysis network
				try {
					analNet.compute(t);
					simNet.compute(t);
					analSimNet.compute(t);
				} catch (Exception e) {
					e.printStackTrace();
				}				
				t++ ;
			}
			System.out.println("finish " + nameFile + " " + new Date().toString()  + " step " + t + " seed " + lSeed.getListSeeds().size() + " node " + lNet.getGraph().getNodeCount()+ "\n");
		
			// close files	
			try {
				analNet.closeFileWriter();
				analSimNet.closeFileWriter();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	
	}

	public static void main ( String[] args ) throws FileNotFoundException, UnsupportedEncodingException {
		
		storeInfo sI = new storeInfo(path, "infoSim") ;
		sI.addLine("// Simulation ---------------------------------------------");

		sI.addLines(new String[] {
				"//Simulation info ----------------" ,
				"number of thread : " + numThread ,
				"step max , store , analysis : " + stepMax + " " + stepToStore + " " + stepToAnalyze ,
				"increm f , k : " +  incremFeed + " " + incremKill ,
				"min and max feed : " + minFeed + " " + maxFeed   ,
				"min and max kill : " + minKill + " " + maxKill ,
				"number of simulation : " + numberOfSimulations ,
				" ",
				"// RD info --------------------" ,
				"size of grid x ,y : " +sizeGridX + " " + sizeGridY , 
				"val initialization : " + initVal0 + " "  + initVal1 ,
				"val perturbation : " + perturVal0 + " " + perturVal1 ,
				"diffusion Da , Db , type diffusion : " + Da + " " + Db +" "+tyDif ,
				"cech values of Rd when reach bord, delta to ceck: " + ceckReachBord +" , " +  deltaCech ,
				" ",
				"// vectorField info --------------" ,
				"number of vectorField : " + numVfs ,
				" " ,
				"// seed and network init ------",
				" number of initial nodes : " + numNodes ,
		});
		sI.createInfo();
		
		System.out.println("size of grid x ,y : " +sizeGridX + " " + sizeGridY ); 
		System.out.println("step max , store , analysis : " + stepMax + " " + stepToStore + " " + stepToAnalyze );
		System.out.println("increm f , k : " +  incremFeed + " " + incremKill);
		System.out.println("min and max feed : " + minFeed + " " + maxFeed ) ;
		System.out.println("min and max kill : " + minKill + " " + maxKill );
		System.out.println("//----------------------------------------------------" + "\n");
	
		int a = 0 ;
		while ( a < numThread ) {
			multiThread m = new multiThread(a++);
			m.start();
	
		}
	}
	
	/**
	 * get list of parameters for corresponding thread
	 * @param params
	 * @param numTreadTot
	 * @param th
	 * @return
	 */
	public static ArrayList<double[]> getParamsForThread (  ArrayList<double[]> params ,int numTreadTot , int th) {
		ArrayList<double[]> list = new ArrayList<double[]> ();
		int size = params.size(),
				pos = size / numTreadTot * th , 
				lim = 0 ;
		if ( th == numTreadTot - 1 ) 
			lim = size ;
		else 
			lim = size / numTreadTot * (th+1) ;
		while ( pos < lim ) 
			list.add(params.get(pos++));
		
			return list ;
	}
	

	/**
	 * get list of parameters
	 * @param vals0
	 * @param vals1
	 * @return
	 */
	public static ArrayList<double[]>  getListParams ( boolean doShuttle , double [] vals0 , double [] vals1 ) {		
		ArrayList<double[]> list = new ArrayList<double[]> () ;
		for ( double val0 : vals0 )
			for ( double val1 : vals1 ) 
				list.add(new double [] {val0,val1} );	
		
		if ( doShuttle ) 
			 Collections.shuffle(list);
		
			return list;
	}
	
	/**
	 * get list of values from min to max with increm
	 * @param min
	 * @param max
	 * @param increm
	 * @return
	 */
	public static double[] getArrayVals (double min , double max , double increm  ) {
		int num = (int) (( max - min ) / increm) + 1 ;	
		double [] arr = new double[num];
		int pos = 0 ;
		while ( pos < num ) {
			arr[pos] =  (double) Math.round( (min + increm * pos) *1000 )/1000 ;
			pos++;
		}
		return arr ;
	}
	
	

}
