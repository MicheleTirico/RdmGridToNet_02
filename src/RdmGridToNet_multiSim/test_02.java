package RdmGridToNet_multiSim;

import java.util.Date;

import RdmGridToNet_multiSim.framework.typeDiffusion;
import RdmGridToNet_multiSim.framework.typeRadius;
import viz.Viz;

	

public class test_02 {

	 
	private static cell[][] cells ;
	
	public static void main(String s[]) {
		 layerRd lRd = new layerRd(1, 1, 100, 100 , typeRadius.circle ) ;
		 lRd.initializeCostVal(1,0);
		 lRd.getCell(50, 50).setVals(1, 1); 
		 cells = lRd.getCells();
	
		 //	 System.out.println("fin init val cost " + (new Date()).toString());
		
		

//		System.out.println("fin set dif " + (new Date()).toString());
		
		
		 
		 
		 double k = 0.02 ;
		 while ( k < 0.05) {	
			
			layerRd local = new layerRd(1, 1, 100, 100 , typeRadius.circle ) ;
			local.initializeCostVal(1,0);
			System.out.println(local.getCells());
			local.setDiffusion(0.2, 0.1, typeDiffusion.mooreCost);
			
			local.getCell(50, 50).setVals(1, 1); //		System.out.println("fin set pert " + (new Date()).toString());
			local.setFeedAndKill(0.03, 0.03);	 //			System.out.println("fin set kill and feed " + (new Date()).toString());
			Viz viz_01 = new Viz(local);
			int t = 0 ;
			
			System.out.println("start sim " + (new Date()).toString());
			 while ( t < 200) {
				 local.updateLayer();
				 viz_01.step();
			//	 System.out.println(t);
				 
				 t++;
			 }
			 
			 System.out.println("fin sim " + (new Date()).toString());
			 k = k + 0.01;
		 }
		
		 

		 
		
		 
		
	 }
	 
}