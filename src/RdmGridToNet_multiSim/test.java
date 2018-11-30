package RdmGridToNet_multiSim;

import RdmGridToNet_multiSim.framework.typeDiffusion;
import RdmGridToNet_multiSim.framework.typeRadius;
import viz.Viz;

	
public class test {
	
	static layerRd lRd_01 = new layerRd(1, 1, 200, 200 , typeRadius.circle ) ;
	static layerRd lRd_02 = new layerRd(1, 1, 200, 200 , typeRadius.circle ) ;
	
	 public static void main(String s[]) {
		 
	 
		 lRd_01.initializeCostVal(1,0);	
		 lRd_01.setDiffusion(0.2, 0.1, typeDiffusion.mooreCost);
		 lRd_01.setFeedAndKill(0.03, 0.03);	 
		 lRd_01.getCell(50, 50).setVals(1, 1);
		 Viz viz_01 = new Viz(lRd_01);
		 
		 
		 lRd_02.initializeCostVal(1,0);	
		 lRd_02.setDiffusion(0.2, 0.1, typeDiffusion.mooreCost);
		 lRd_02.setFeedAndKill(0.01, 0.01);	 
		 lRd_02.getCell(50, 50).setVals(1, 1);
		 Viz viz_02 = new Viz(lRd_02);
		 
		 int t = 0 ;
		 
		 while ( t < 500) {
			 lRd_01.updateLayer();
			 viz_01.step();
			 
			 lRd_02.updateLayer();
			 viz_02.step();
			 
			 System.out.println(t);
			 t++;
		 }
		 
		 t = 0 ;
		 
		 while ( t < 100) {
	
			 
			
			 System.out.println(t);
			 t++;
		 }
			 
	 }
	 
}