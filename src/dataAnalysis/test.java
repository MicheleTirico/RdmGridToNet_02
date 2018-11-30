package dataAnalysis;

import java.io.IOException;
import java.util.Arrays;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

import dataAnalysis.analysis.indicator;
import graphstream_dev_toolkit.toolkit;

public class test    {
	
	static String path = "C:\\Users\\frenz\\ownCloud\\RdmGrid_exp\\test";
	public static void main ( String[] args ) throws IOException {
		
		Graph graph1 = new SingleGraph("test1") ;
		analyzeNetwork aN1 = new analyzeNetwork(true, 10, graph1, path,  "test1", "name1");
	
		toolkit.createGraphRandom(graph1, 30);
		aN1.setIndicators(Arrays.asList(indicator.averageDegree, indicator.gammaIndex));
		
		aN1.initAnalysis();
		aN1.compute(10);
		aN1.closeFileWriter();
		
		Graph graph2 = new SingleGraph("test2") ;
		analyzeNetwork aN2 = new analyzeNetwork(true, 10, graph2,  path,  "test2", "name2");
		toolkit.createGraphRandom(graph2, 50);
		aN2.setIndicators(Arrays.asList(indicator.averageDegree, indicator.gammaIndex ));
		aN2.initAnalysis();
		aN2.compute(10);
		aN2.closeFileWriter();
		
		graph1.display(true) ;
	}

		
	

}
