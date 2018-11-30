package dataAnalysis;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.graphstream.graph.Graph;
import dataAnalysis.indicatorSet.indicator;;

public class analyzeNetwork  {
	
	private ArrayList<indicator> listIndicators = new ArrayList<indicator> ();
	private Graph graphToAnalyze ;
	private boolean run , isSim , addTitleCSV;
	private double stepToAnalyze ;
	private String 	header , nameFile  , path  ;
	private String[] keys , values  ;
	private handleFolder hF ;
	private indicatorSet iS = new indicatorSet(); 
	
	public analyzeNetwork () throws IOException {
		this(false ,false ,0 ,null, null, null, null);
	}
	
	public analyzeNetwork (boolean run , boolean isSim, double stepToAnalyze ,Graph graphToAnalyze, String path , String nameFolder , String nameFile ) throws IOException {
		this.run = run ;
		this.isSim = isSim ;
		this.stepToAnalyze = stepToAnalyze ;
		this.graphToAnalyze = graphToAnalyze ;
		this.path = path +"\\"+ nameFolder + "\\";
		this.nameFile = nameFile;
		if ( run ) {
			hF = new handleFolder(path) ;
			hF.createNewGenericFolder(nameFolder); 
			iS.setIsSim(isSim);
		}
	}
	
	public void setupHeader_02 (boolean addTitleCSV , Map startParam ) {
		this.addTitleCSV = addTitleCSV;	
		keys = new String [startParam.size() + 1 ] ;
		values = new String [startParam.size() + 1  ];
		int pos = 0 ;
		for ( Object key : startParam.keySet() ) {
			keys[pos] = key.toString();
			values[pos] = startParam.get(key).toString() ;
			pos++ ;
	 	}
		keys[pos] = ";";
		values[pos] = ";";
	} 
	
	public void setupHeader (boolean addTitleCSV , Map startParam ) {
		this.addTitleCSV = addTitleCSV;	
		keys = new String [11] ;
		values = new String [11];
		
		int pos = 0 ; 
		for ( Object key : startParam.keySet() ) {
			keys[pos] = key.toString();
			values[pos] = startParam.get(key).toString();
			pos++ ;
			
	 	}
		while ( pos < 10 ) {
			keys[pos] = "0";
			values[pos] = "0";
			pos++;
		} 
		keys[pos] = ";";
		values[pos] = ";";
	}
	public void initAnalysis ( ) throws IOException {
		if ( run )
			for ( indicator in : listIndicators ) {
				in.setId(in.toString());
				String pathFile = path +nameFile+ in + ".csv" ;
				
				// handle file if exist 
				File f = new File(pathFile); 
				if ( f.exists() )  
					f.delete();
	
				// create new file 
				iS.setPath(pathFile);		
				iS.setFw(in);
				in.setHeader();
				FileWriter fw = iS.getFw(in) ; 
				if ( addTitleCSV )
					expCsv.addCsv_header( fw , in.toString() ) ;
				if ( keys.length != 0 ) { 
					expCsv.writeLine(fw, Arrays.asList( keys ) , ';' ) ;	
					expCsv.writeLine(fw, Arrays.asList( values ) , ';' ) ;		
				}
					
				header = in.getHeader(); 
				expCsv.addCsv_header( fw , header ) ;
			}
	}
	
	public void compute (int t) throws IOException , Exception {
		if ( run &&  t / stepToAnalyze - (int)(t / stepToAnalyze ) < 0.01 ) 
			for ( indicator in : listIndicators ) {
				iS.setGraph(graphToAnalyze); 
				FileWriter fw = iS.getFw(in) ; 
				if ( in.getIsList() ) {					
					double[] valArr = iS.getValueArr(in);
					String[] valList = castArrValToString(valArr, t, (int) in.getFrequencyParameters()[0]); // System.out.println(in + " " + fw );
					expCsv.writeLine(fw, Arrays.asList( valList ) , ';' ) ;		
				}
				else {
					double val = iS.getValue(in) ;
					expCsv.writeLine(fw, Arrays.asList( Double.toString(t) , Double.toString(val) ) , ';' ) ;			
				}
			}
	}
		
	public void compute () throws IOException , Exception {
		if ( run  ) 
			for ( indicator in : listIndicators ) {
				iS.setGraph(graphToAnalyze); 
				FileWriter fw = iS.getFw(in) ; 
				if ( in.getIsList() ) {
					double[] valArr = iS.getValueArr(in);
					String[] valList = castArrValToString(valArr, 0, (int) in.getFrequencyParameters()[0]);
					expCsv.writeLine(fw, Arrays.asList( valList ) , ';' ) ;	
				}
				else {
					double val = iS.getValue(in) ;
					expCsv.writeLine(fw, Arrays.asList( Double.toString(0) , Double.toString(val) ) , ';' ) ;			
				}
			}
	}
	
	// close file writer
	public void closeFileWriter () throws IOException {
		if  ( run ) 
			for ( indicator in : listIndicators )
				iS.getFw(in).close();
	}		
	
// GET METHODS --------------------------------------------------------------------------------------------------------------------------------------
	public boolean getIsSim ( ) {
		return isSim ;
	}
		
// SET METHODS --------------------------------------------------------------------------------------------------------------------------------------
	public void setIndicators ( indicator indicator ){	
		listIndicators.add(indicator);
	}
	
	public void setIndicators ( Collection<? extends indicator> list ) {
		listIndicators.addAll(list) ;
	}
	
	// get list ( string ) of values for indicator 
	public String [] castArrValToString ( double[] valArr , int t , int numVals) {
		String[] listString  = new String[numVals] ;
		int pos = 1 ;
		listString[0] = Integer.toString(t) ; 
		while ( pos< valArr.length ) {
			listString[pos] = Double.toString(valArr[pos]);
			pos++;
		}
		return listString ;
	}
}