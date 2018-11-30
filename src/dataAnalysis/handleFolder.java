package dataAnalysis;

import java.io.File;

public class handleFolder extends analysis {

	private String path ; 
	
	public handleFolder( String path ) {
		this.path = path ;
	}
	
	
	public String createNewGenericFolder ( String nameFolder ) {	 
		path = path+"\\"+ nameFolder;
		File file = new File(path);
		file.mkdir() ;	
		return path;
	}
	
	public String createNewGenericFolder ( String  path ,  String nameFolder ) {	 
		path = path+"\\"+ nameFolder;
		File file = new File(path);
		file.mkdir() ;	
		return path;
	}
	
	
}
