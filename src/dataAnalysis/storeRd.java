package dataAnalysis;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import org.graphstream.graph.Graph;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import RdmGridToNet.cell;
import RdmGridToNet.framework;
import dataAnalysis.indicatorSet.indicator;

public class storeRd extends framework {
	
	private boolean run ;
	private double stepToStore  ;
	private String path , nameFolder , nameFile;
	private handleFolder hF ;
	private FileWriter fwRd ;
	private int numCellX ,numCellY ;
	private int[] sizeGrid ;
	public enum whichMorpToStore {a , b , both}
	private whichMorpToStore whichMorpToStore ;
	private cell[][] cells ;
	
	public storeRd (boolean run, whichMorpToStore whichMorpToStore ,double stepToStore  , String path , String nameFolder , String nameFile ) throws IOException {
		this.run = run ;
		this.whichMorpToStore = whichMorpToStore ;
		this.stepToStore = stepToStore;		
		this.nameFolder= nameFolder;
		this.nameFile = nameFile ;
		this.path = path +"\\"+ nameFolder + "\\" + nameFile + ".csv";
		if ( run ) {
			hF = new handleFolder(path) ;
			hF.createNewGenericFolder(nameFolder); 
		}
	}
	
	public void initStore() throws IOException {
		if ( run ) {
			cells = lRd.getCells();
			sizeGrid = lRd.getSizeGrid();
			numCellX = sizeGrid[0];
			numCellY = sizeGrid[1];
			String header = getHeader () ;
			fwRd = new FileWriter(path);
			expCsv.addCsv_header(fwRd, header);	
		}
	}
	
	
	public void storeStepRd (int t) throws IOException {
		if ( run &&  t / stepToStore - (int)(t / stepToStore ) < 0.01 
				)  {
			double val , val1 ,val2 ;
			String[] vals ;
			switch (whichMorpToStore) {
			case a: {
				vals = getListValues(morphogen.a , t);
				expCsv.writeLine(fwRd, Arrays.asList( vals ) , ';' ) ;		
			} break; 

			case b : {
				vals = getListValues(morphogen.b , t);
				expCsv.writeLine(fwRd, Arrays.asList( vals ) , ';' ) ;		
			} break;
			
			case both : {
				vals = getListValues(morphogen.a , t);
				expCsv.writeLine(fwRd, Arrays.asList( vals ) , ';' ) ;	
				vals = getListValues(morphogen.b, t );
				expCsv.writeLine(fwRd, Arrays.asList( vals ) , ';' ) ;		
			} break;
			}
		}
	}
	
	public void closeFileWriter () throws IOException {
		if  ( run ) 
			fwRd.close();
	}		
	
// GET METHODS --------------------------------------------------------------------------------------------------------------------------------------
	
	// create header
	private String getHeader() {
		String h = "Rd_t_" + whichMorpToStore.toString()+";";
		
		for ( int x = 0 ; x < sizeGrid[0]; x++) 
			for ( int y = 0 ; y < sizeGrid[1]; y++) 
				h = h + "x-"+ x + "_y-" + y + ";";

		return h;
	}
	
	private String[]  getListValues ( morphogen m , int t) {
		String [] vals = new String[numCellX * numCellY +1] ;
		vals[0] = Integer.toString(t);
		double val ;
		int pos = 1 ;
		
	
		for ( int x = 0 ; x < sizeGrid[0]; x++) 
			for ( int y = 0 ; y < sizeGrid[1]; y++) {
				vals[pos] = Double.toString(lRd.getValueMorphogen(x, y, m));
				pos++;
			}
		return vals ;
	}	
}
