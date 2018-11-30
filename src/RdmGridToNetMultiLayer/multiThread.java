package RdmGridToNetMultiLayer;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.graphstream.graph.Graph;

import RdmGridToNetMultiLayer.framework.RdmType;
import RdmGridToNetMultiLayer.layerCell.typeDiffusion;
import RdmGridToNetMultiLayer.layerMaxLoc.typeComp;
import RdmGridToNetMultiLayer.layerMaxLoc.typeInit;
import RdmGridToNetMultiLayer.layerSeed.handleLimitBehaviur;
import RdmGridToNetMultiLayer.vectorField.typeRadius;
import RdmGridToNetMultiLayer.vectorField.typeVectorField;
import dataAnalysis.analyzeNetwork;
import dataAnalysis.storeNetwork;
import dataAnalysis.indicatorSet.indicator;
import layerViz.vizLayerCell;


public class multiThread extends Thread {


	private int th ; 
	
	public multiThread(int th) {
		this.th = th;
	}
	
	static int numThread = 4 ;
	
	// common parameters
		private static int stepToStore = 10 , 
					stepToAnalyze = 10 ,
					stepToPrint = 100 ,
					stepMax = 5000 ;	

		// parameters multi sim 
		private static  double incremKill = 0.01 , 
				incremFeed = 0.01 ,
				minFeed = 0.01 ,
				maxFeed = 0.09 , 
				minKill = 0.01  ,
				maxKill = 0.09 ;
		
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
//		private static morphogen m = morphogen.b;
		private static double r = 2 ,
				minDistSeed = 1 , 
				alfa = 2 ;
//		private static typeVectorField tvf = typeVectorField.slopeDistanceRadius;
		
		// initialize circle seeds
		private static int numNodes = 10, 
				radiusRd = 2 , 
				radiusNet = 4 ;
		
	 
	double [] arrayF = getArrayVals(minFeed, maxFeed, incremFeed);
	double [] arrayK = getArrayVals(minKill , maxKill , incremKill );
	ArrayList<double[]> params = getListParams(arrayF, arrayK);
	
	public void run (  ) {
		System.out.println("start " + new Date().toString() );
		ArrayList<double[]> listParams = getParamsForThread(params,numThread, th);
	//	System.out.println(th + " " + listParams);
		for ( double[] fk : listParams) {
			double f = fk[0], 
					k = fk[1];
			// bucket set
			bucketSet bks = new bucketSet(1, 1, sizeGridX, sizeGridY ); 
			bks.initializeBukets(); 
	
			// layer Rd
			layerCell lRd = new layerCell(1, 1, sizeGridX, sizeGridY ,2,5) ;
			lRd.initializeCostVal(new double[] {1,0});
			lRd.setValueOfCellAround(new double[] {1, 1}, sizeGridX/2,sizeGridY/2 ,3 );

	//		lRd.setGsParameters(fk[0] , fk[1] , 0.2, 0.1, typeDiffusion.mooreCost);
			lRd.setGsParameters(f , k , 0.2, 0.1, typeDiffusion.mooreCost);
			
			// layer max local
			layerMaxLoc lMl = new layerMaxLoc(true,lRd,true, typeInit.test, typeComp.wholeGrid ,1);
			lMl.initializeLayer();
			
			// layer bumps
			layerCell lBumps = new layerCell(1, 1, sizeGridX, sizeGridY ,3,3) ;
			lBumps.initCells();		
		//	lBumps.setGridInCoordsLayer(lBumps.getBumbsFromPosition (1 , 1 , 1));
			lBumps.setGridInValsLayer(lBumps.getBumbsFromPosition ( 1 , 1 , 10) , 0);		
			
			// layer infinite paraboloid 
			layerCell lParab = new layerCell(1, 1, sizeGridX, sizeGridY ,3,3) ;
			lParab.initCells();
			lParab.setGridInValsLayer(lParab.getInfiniteParaboloid(0, .2000, .200, new double[] {sizeGridX/2 ,sizeGridY/2 ,0} ), 0);
			
			// vector field Rd
			vectorField vfRd = new vectorField(lRd, 1, 1 , sizeGridX, sizeGridY, typeVectorField.slopeDistanceRadius) ;
			vfRd.setSlopeParameters( 1 , r, alfa, true, typeRadius.circle);
			
			vectorField vfBumps = new vectorField(lBumps,  1, 1 , sizeGridX, sizeGridY, typeVectorField.minVal);
			vfBumps.setMinDirectionParameters(0);
			
			vectorField vfParab = new vectorField(lParab, 1, 1, sizeGridX , sizeGridY, typeVectorField.interpolation);
			vfParab.setInterpolationParameters( 0, 1, 2);
	//		vfParab.setSlopeParameters( 0 , r, alfa, true, typeRadius.circle);
			
			layerNet lNet = new layerNet("net") ;
				
			// layer Seed
			layerSeed lSeed = new layerSeed(handleLimitBehaviur.stopSimWhenReachLimit, new vectorField[] { vfRd , vfParab
																							 }
																							, new double[] { 1 , 0.0 }
			);
			lSeed.initSeedCircle(numNodes, radiusNet, sizeGridX/2, sizeGridY/2);	
		//	framework.initCircle(perturVal0,perturVal1,numNodes , sizeGridX/2 ,sizeGridY/2, 2 , radiusNet );				
		//	lNet.getGraph().display(false) ;
		//	lNet.setLengthEdges("length" , true );
			
			Graph netGr = lNet.getGraph();
				
			NumberFormat nf = NumberFormat.getNumberInstance();
			nf.setMaximumFractionDigits(3);
			String nameFile = "f-"+ nf.format(f)+"_k-"+ nf.format(k)+"_";
			System.out.println(nameFile + (new Date().toString()));
		
		}
		
		System.out.println("finish " + new Date().toString() );

	
	
	}

	public static void main ( String[] args ) {
		
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
	public static ArrayList<double[]> getParamsForThread ( ArrayList<double[]> params ,int numTreadTot , int th) {
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
	public static ArrayList<double[]>  getListParams ( double [] vals0 , double [] vals1 ) {		
		ArrayList<double[]> list = new ArrayList<double[]> () ;
		for ( double val0 : vals0 )
			for ( double val1 : vals1 ) 
				list.add(new double [] {val0,val1} );	
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
			arr[pos] =  (double) Math.round( (min + increm * pos) *100 )/100 ;
			pos++;
		}
		return arr ;
	}
	
	

}
