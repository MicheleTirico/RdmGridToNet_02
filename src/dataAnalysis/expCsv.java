package dataAnalysis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class expCsv extends analysis {

	public Map mapExp = new HashMap() ;

	private static final char DEFAULT_SEPARATOR = ',';
    
    public static void writeLine(Writer w, List<String> values) throws IOException {
        writeLine(w, values, DEFAULT_SEPARATOR, ' ');
    }

    public static void writeLine(Writer w, List<String> values, char separators) throws IOException {
        writeLine(w, values, separators, ' ');
    } 
    
    public static void writeLine(Writer w, String[] values, char separators) throws IOException {
    	List<String> list = Arrays.asList(values);
        writeLine(w, list, separators, ' ');
    }
    

    public static void addCsv_header(Writer w, String header ) throws IOException  {
        
    	  StringBuilder sb = new StringBuilder();
    	  sb.append(followCVSformat(header));
    	  sb.append("\n");
    	  w.append(sb.toString());  
    }
    
    private static String followCVSformat(String value) {

        String result = value;
        
        if (result.contains("\""))  
            result = result.replace("\"", "\"\"");
        
        return result;
    } 

    public static void writeColumn ( int posRow , String pathFile, List<String> values, char separators) throws IOException {
    	
    	StringBuilder sb = new StringBuilder();	
    	BufferedReader br = new BufferedReader(new FileReader(pathFile)) ;
    	
    	String line ; 
    	int pos = 0 ;
    	while ((line = br.readLine() ) != null) {
//	    		
    		if ( pos == posRow)
    			line = line + ";" + values.get(posRow);
    		pos++ ;
    		
    	}
    }
    
    private static void writeLine(Writer w, List<String> values, char separators, char customQuote) throws IOException {
    	
        boolean first = true;
        //default customQuote is empty
        if (separators == ' ') 
            separators = DEFAULT_SEPARATOR;
      
        StringBuilder sb = new StringBuilder();
        
        for (String value : values) {
            
        	if (!first) 
                sb.append(separators);
            
            if (customQuote == ' ') 
                sb.append(followCVSformat(value));
            else 
                sb.append(customQuote).append(followCVSformat(value)).append(customQuote);
            
            first = false;
        }
        sb.append("\n");
        w.append(sb.toString());
    }    
}

