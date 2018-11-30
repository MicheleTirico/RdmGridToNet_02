package dataAnalysis;

import java.io.IOException;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSinkDGS;

public class storeNetwork extends analysis {
	
	private boolean run ;
	private double stepToStore ;
	private Graph graph = new SingleGraph("grToAn");	
	private handleFolder hF ;
	private String 	header , nameFile  , path , pathNetStore ,
		nameFileStart , nameFileStep ,
					pathStartDSG , pathStepDSG ;
	 
	private FileSinkDGS fsd = new FileSinkDGS();
	
	public storeNetwork ( ) throws IOException {
		this(false , 0.0 , null, null, null, null);
	}

	public storeNetwork (boolean run , double stepToStore  , Graph graph, String path , String nameFolder , String nameFile ) throws IOException {
		this.run = run ;
		this.stepToStore = stepToStore  ;
		this.graph = graph ;
		this.path = path ;
		this.nameFile = nameFile;
		if ( run ) {
			hF = new handleFolder(path) ;
			pathNetStore = hF.createNewGenericFolder(nameFolder);
			nameFileStart = nameFile + "Start.dsg";
			nameFileStep = nameFile + "Step.dsg";
			pathStartDSG = path +"\\" + nameFolder +"\\"+ nameFileStart ;
			pathStepDSG = path +"\\" + nameFolder +"\\"+ nameFileStep ;
		}
	}
	
	public void initStore ( ) throws IOException {
		if ( run ) {
			graph.write(fsd, pathStartDSG);
			graph.addSink(fsd);
			fsd.begin(pathStepDSG);
		}
	}
	
	public void storeDSGStep ( int t )  throws Exception {
		if ( run && t / stepToStore - (int)(t / stepToStore ) < 0.01)
			graph.stepBegins(t);		
	}
	
	public void closeStore ( ) throws IOException {
		if ( run )
			fsd.end();
	}
}
