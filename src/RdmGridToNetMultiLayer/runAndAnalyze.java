package RdmGridToNetMultiLayer;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

import RdmGridToNetMultiLayer.layerCell.typeDiffusion;

import RdmGridToNetMultiLayer.layerMaxLoc.typeComp;
import RdmGridToNetMultiLayer.layerMaxLoc.typeInit;
import RdmGridToNetMultiLayer.layerSeed.handleLimitBehaviur;
import RdmGridToNetMultiLayer.vectorField.typeRadius;
import RdmGridToNetMultiLayer.vectorField.typeVectorField;
import dataAnalysis.indicatorSet.indicator;
import dataAnalysis.analyzeNetwork;
import dataAnalysis.storeNetwork;
import layerViz.vizLayerCell;
import layerViz.vizLayerNet;
import layerViz.vizLayerNet.stylesheet;

public class runAndAnalyze extends framework  {
	
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
//	private static morphogen m = morphogen.b;
	private static double r = 2 ,
			minDistSeed = 1 , 
			alfa = 2 ;
//	private static typeVectorField tvf = typeVectorField.slopeDistanceRadius;
	
	// initialize circle seeds
	private static int numNodes = 20, 
			radiusRd = 2 , 
			radiusNet = 4 ;

	public static void main(String[] args) throws Exception {	
		
		// bucket set
		bks = new bucketSet(1, 1, sizeGridX, sizeGridY );
		bks.initializeBukets();

		// layer Rd
		lRd = new layerCell(1, 1, sizeGridX, sizeGridY ,2,5) ;
		lRd.initializeCostVal(new double[] {1,0});
		lRd.setValueOfCellAround(new double[] {1, 1}, sizeGridX/2,sizeGridY/2 ,3 );
	
//		lRd.setValueOfCellAround(new double[] {1, 1}, 50 , 50  ,3 );
//		lRd.setValueOfCellAround(new double[] {1, 1}, 150 , 150 ,3 );
		
		double [] fk = getRdType(RdmType.movingSpots);
		lRd.setGsParameters(fk[0] , fk[1] , 0.2, 0.1, typeDiffusion.mooreCost);
	//	lRd.setGsParameters(0.001 , 0.023 , 0.2, 0.1, typeDiffusion.mooreCost);
		
		// layer max local
		lMl = new layerMaxLoc(true,lRd,true, typeInit.test, typeComp.wholeGrid ,1) ;
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
		
//		for ( cell c : lParab.getListCell() ) {
//			System.out.println(c.getX() + " " + c.getY() + " " + c.getVals()[0]  );
//		}
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
		lSeed = new layerSeed(handleLimitBehaviur.stopSimWhenReachLimit, new vectorField[] { vfRd
																						 , vfParab }
																						, new double[] { 1 , .1 }
		);
		lSeed.initSeedCircle(numNodes, radiusNet, sizeGridX/2, sizeGridY/2);
			
//		lSeed.initSeedCircle(numNodes, radiusNet, 50 ,50);		
//		lSeed.initSeedCircle(numNodes, radiusNet, 150 , 150 );
		
		// initCircle(perturVal0,perturVal1,numNodes , sizeGridX/2 ,sizeGridY/2, 2 , radiusNet );		
		
		Graph netGr = lNet.getGraph() ;
		netGr.display(false) ; 
		
		lNet.setLengthEdges("length" , true );
		
		// viz 
	
		vizLayerCell vLRd = new vizLayerCell(lRd, 1);	
	//	vizLayerCell vLc = new vizLayerCell(lBumps, 0);	
	//	vizLayerCell vLparab = new vizLayerCell(lParab, 0);	
	//	vLc.step();
	//	vLparab.step();
		
		// setup viz netGraph
		vizLayerNet netViz = new vizLayerNet( netGr ,stylesheet.manual , "seed", 1) ;
		netViz.setupIdViz(false , netGr, 20 , "black");
		netViz.setupDefaultParam (netGr, "black", "black", 5 , 0.5 );
		netViz.setupVizBooleanAtr(true, netGr, "black", "red" , false , false ) ;
		netViz.setupFixScaleManual( true , netGr, sizeGridX , 0);

		int t = 0 ; 
		while ( t <= stepMax //  && ! lSeed.getListSeeds().isEmpty() 
				//&& lNet.seedHasReachLimit == false 
				) {	
			System.out.println( "-------------------" + t +"-------------------");
			System.out.println(" seed " + lSeed.getListSeeds().size() + "\n node " + lNet.getGraph().getNodeCount()+ "\n");
			
			// update layers
			lRd.updateLayer();
			lMl.updateLayer();
			lNet.updateLayers( 0 ,true,1);
			
			 System.out.println("maxLoc " + lMl.getNumMaxLoc()	) ;
			
			// viz
			vLRd.step();
			
			t++ ;
		}
			

	
		
	}		
	
	
}