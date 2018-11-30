package RdmGridToNetMultiThread;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.graphicGraph.GraphPosLengthUtils;

public class layerCell extends framework {

	private double sizeX, sizeY;
	private int numCellX, numCellY , numVals, numIsTest;
	private cell[][] cells ;
	private boolean whatDoReachBord ;
	private boolean hasReachBord = false ;
	private double[ ] initVals ;
	private double deltaReach ;
	/**
	 * parameters for Rd 
	 */
	private static double f ,  k ,  Da,  Db ;
	public enum typeDiffusion { mooreCost, mooreWeigthed , vonNewmannCost }	
	private typeDiffusion typeDiffusion ;
	
	public enum typeNeighbourhood { moore, vonNewmann , m_vn }	
	protected  ArrayList<cell> listCell = new ArrayList<cell> ();
	
	public layerCell ( ) {
		this(0,0,0,0 ,0 ,0 );	
	}

	public layerCell(double sizeX, double sizeY , int numCellX, int numCellY ,  int numVals ,int numIsTest ) {
		this.sizeX = sizeX ;
		this.sizeY = sizeY ;
		this.numCellX = numCellX ;
		this.numCellY = numCellY ;
		this.numVals = numVals ;
		this.numIsTest = numIsTest;
		cells = new cell[numCellX][numCellY];
	}

// INITIALIZATION GRID ------------------------------------------------------------------------------------------------------------------------------
	public void initializeCostVal ( double[] vals ) {
		initVals = vals ;
		for (int x = 0; x< numCellX; x++)
			for (int y = 0; y<numCellY; y++) {
				cell c = new cell(x,y, new double[3],vals,new boolean[numIsTest] );
				c.initCoords( sizeX, sizeY );
				cells[x][y] = c ;
				putCellInList(c);			
			}
	}
	
	public void initializeRandomVals ( int[] seedRd, double[] minVal,  double[] maxVal ) {	
		int pos = 0 ;
		while ( pos < seedRd.length ) {
			Random rd = new Random( seedRd[pos] );
			for (int x = 0; x<numCellX; x++)
				for (int y = 0; y<numCellY; y++) {
					double[] vals = new double[seedRd.length] ;
					int i = 0 ; 
					while ( i < seedRd.length ) {
						vals[i] =   minVal[i] + (maxVal[i] - minVal[i]) * rd.nextDouble() ;
						i++;
					}
					cell c = new cell(x,y,new double[3],vals,new boolean[numIsTest]);
					c.initCoords( sizeX, sizeY );
					cells[x][y] = c ;
					putCellInList(c);					
				}
			pos++ ;
		}
	}	
	
	public void initCells ( ) {
		for (int x = 0; x<numCellX; x++)
			for (int y = 0; y<numCellY; y++) {
				cell c = new cell(x,y, new double[3],new double[numVals],new boolean[numIsTest]);
				c.initCoords( sizeX, sizeY );
				cells[x][y] = c ;
				putCellInList(c);			
			}
	}
	
// SET RD PARAMETERS --------------------------------------------------------------------------------------------------------------------------------
	// set initial parameters of gray scott model
	public void setGsParameters ( double f , double k , double Da, double Db, typeDiffusion typeDiffusion) {
		this.f = f ;
		this.k = k ;
		this.Da = Da ;
		this.Db = Db ;
		this.typeDiffusion = typeDiffusion;
	}
	
// COMPUTE VALS -------------------------------------------------------------------------------------------------------------------------------------	
	public void computeBumbs ( double incremX , double incremY , double incremZ ) {
		for ( cell c : listCell ) {
			double[] coord = c.getCoords() ;
			double val = incremZ * Math.sin(coord[0]*incremX) * Math.cos( coord[1]*incremY ) ;
			c.setVal(0, val);
		}
	}
	
	public double[][] getInfiniteParaboloid ( double incremZ , double a , double b , double[] coordVertex ) {
		double[][] grid = new double [numCellX][numCellY];
		for ( cell c : listCell ) {
			double[] coord = c.getCoords() ;
			int[] pos = c.getPos();		
			grid[pos[0]][pos[1]] = a *  Math.pow( pos[0] - coordVertex[0] , 2 ) + b  *  Math.pow(pos[1]  - coordVertex[1], 2 ) + coordVertex[2] ;
//			grid[pos[0]][pos[1]] = incremZ * ( Math.pow(pos[0], 2) / a + Math.pow(pos[1], 2) / b  ) ;
		}
		return grid ;
	}
	
	/**
	 * return a grid of double with value z coords 
	 * z is computed from position of cell 
	 * @param incremX
	 * @param incremY
	 * @param incremZ
	 * @return
	 */
	public double[][] getBumbsFromPosition ( double incremX , double incremY , double incremZ  ) {
		double[][] grid = new double [numCellX][numCellY];
		for ( cell c : listCell ) {
			double[] coord = c.getCoords() ;
			int[] pos = c.getPos();		
			grid[pos[0]][pos[1]] = incremZ * Math.sin(pos[0]*incremX) * Math.cos( pos[1]*incremY) ;
		}
		return grid ;
	}
	
	/**
	 * return a grid of double with value z coords 
	 * z is computed from coords of cell 
	 * @param incremX
	 * @param incremY
	 * @param incremZ
	 * @return
	 */
	public double[][] getBumbsFromCoords ( double incremX , double incremY , double incremZ  ) {
		double[][] grid = new double [numCellX][numCellY];
		for ( cell c : listCell ) {
			double[] coord = c.getCoords() ;
			int[] pos = c.getPos();		
			grid[pos[0]][pos[1]] = incremZ * Math.sin(coord[0]*incremX) * Math.cos( coord[1]*incremY) ;
		}
		return grid ;
	}
	
	public void setGridInValsLayer ( double[][] grid , int posVal ) {
		for (int x = 0; x<numCellX; x++)
			for (int y = 0; y<numCellY; y++) {
				this.getCell(x,y).setVal(posVal, grid[x][y]);
			}
	}
	
	public void setGridInCoordsLayer ( double[][] grid ) {
		for (int x = 0; x<numCellX; x++)
			for (int y = 0; y<numCellY; y++) {
				this.getCell(x,y).setCoord(2 , grid[x][y]);
			}
	}
	
	// set perturbation 
	public void setValueOfCell ( double[] vals , int cellX, int cellY ) {
		cells[cellX][cellY].setVals(vals);		
	}
	
	// set perturbation in radius
	public void setValueOfCellAround  ( double[] vals , int cellX, int cellY, int radius ) {
		for ( int x = (int) Math.floor(cellX - radius) ; x <= (int) Math.ceil(cellX + radius ) ; x++  )
			for ( int y = (int) Math.floor(cellY - radius ) ; y <= (int) Math.ceil(cellY + radius ) ; y++  ) {
				cells[x][y].setVals(vals);							
			}		
	}
	
	/** 
	 * ceck if RD reach bords
	 */
	public void setReachBord (boolean whatDo , double deltaReach ) {
		whatDoReachBord = whatDo ;
		this.deltaReach = deltaReach ;
	}
	
	// update cells 
	public void updateLayer (  ) {
	//	cechIfReachBord( whatDoReachBord );
		for ( cell c : listCell ) {
			double[] vals = c.getVals();
			double 	val0 = vals[0],
					val1 = vals[1];
		
			double 	diff0 = Da * getDiffusion(typeDiffusion,c ,0 ) ,
					diff1 = Db * getDiffusion(typeDiffusion, c, 1) ,

					react = val0 * val1 * val1 ,
			
					extA = f * ( 1 - val0 ) ,
					extB = ( f + k ) * val1 ;
	
			double	newval0 =  val0 + diff0 - react + extA,
					newval1 =  val1 + diff1 + react - extB;
			
			double [] newVals = new double[] { newval0 ,newval1 } ;
			
			if ( whatDoReachBord ) {
				if ( isCellOnBord(c) ) 					
					if ( Math.abs(newVals[0] - initVals[0]) >= deltaReach )
						hasReachBord = true ;
					else if ( Math.abs(newVals[1] - initVals[1] ) >= deltaReach  )
						hasReachBord = true ;
				
			}
			c.setVals(new double[] {newval0, newval1} );
		}
	}
	

	// get Fick's diffusion 
	private double getDiffusion ( typeDiffusion typeDiffusion, cell c , int pos ) {
		double 	diff = 0 , 
				val = c.getVals()[pos] ,
				valNeig = 0,
				valNeigS = 0 ,	// sum of values of side neighbors 
				valNeigC = 0;	// sum of values of corner neighbors	
		ArrayList<cell> listNeig = new ArrayList<cell>();
		
		switch (typeDiffusion) {
			case mooreCost: {
				listNeig = getListNeighbors(typeNeighbourhood.moore, c.getX(),c.getY()) ;
				
				for ( cell neig : listNeig) 
					 valNeig = valNeig + neig.getVals()[pos];

				diff = -  val + valNeig / listNeig.size()  ;
			}	break;
			
			case vonNewmannCost: {
				listNeig = getListNeighbors(typeNeighbourhood.vonNewmann, c.getX(),c.getY()) ;
				
				for ( cell neig : listNeig) 
					 valNeig = valNeig + neig.getVals()[pos];

				diff = -  val + valNeig / listNeig.size()  ;
			}	break;
			
			case mooreWeigthed : {
				ArrayList<cell> listNeigS = new ArrayList<cell>(getListNeighbors(typeNeighbourhood.vonNewmann, c.getX(), c.getY()));
				ArrayList<cell> listNeigC = new ArrayList<cell>(getListNeighbors(typeNeighbourhood.m_vn, c.getX(), c.getY()));
				
				for ( cell neigS : listNeigS ) 
					valNeigS = valNeigS + neigS.getVals()[pos];

				for ( cell neigC : listNeigC ) 
					 valNeigC = valNeigC + neigC.getVals()[pos];
			
				diff = - val + 0.2 * valNeigS + 0.05 * valNeigC ; 				
			}	break ;
		}
		return diff ; 
	}
	
// GET METHODS --------------------------------------------------------------------------------------------------------------------------------------
	// get center of the world
	protected double[] getCenter () {		
		return new double[] { numCellX * sizeX / 2 , numCellY * sizeY / 2} ;
	}
	
	protected double[] getCellVals ( cell c ) {
		return c.getVals();
	}
	
	protected double[] getCellVals ( int posX , int posY ) {
		return getCell(posX, posY).getVals();
	}
	
	private boolean cechIfReachBord( boolean whatDoReachBord , double valTest , int posVal) {
		ArrayList<cell> listCellsBord = new ArrayList<cell> ();
		listCellsBord.addAll(getListCellBords(whichBord.bottom));
		listCellsBord.addAll(getListCellBords(whichBord.left));
		listCellsBord.addAll(getListCellBords(whichBord.right));
		listCellsBord.addAll(getListCellBords(whichBord.top));
		
		for ( cell c : listCellsBord ) {
			if ( Math.abs(c.getVals()[posVal]) + valTest >=  0.1 ) {
				
			}
		}
		
		return true;
	}
	
	private boolean isCellOnBord ( cell c ) {
		int[] pos = c.getPos();
		if ( pos[0] == 0 || pos[1] == 0 || pos[0] == numCellX || pos[1] == numCellY ) 
			return true ;
		else 
			return false ;
	}
	
	public enum whichBord {top, bottom , left , right } ;
	public ArrayList<cell> getListCellBords (whichBord whichBord){
		ArrayList<cell> list = new ArrayList<cell> ();
		int a = 0;
		switch (whichBord) {
			case top : {
				while ( a < numCellX )
					list.add(cells[numCellX][a]);
			} break;
			
			case bottom : {
				while ( a < numCellX )
					list.add(cells[0][a]);
			}break;
			
			case left :{
				while ( a < numCellY )
					list.add(cells[a][0]);
			}break;
			
			case right : {
				while ( a < numCellY )
					list.add(cells[a][numCellY]);
			} break;
		}
		return list ;
	}

	public boolean getHasReachBord ( ) {
		return hasReachBord;
	}
	
// GET NEIGHBORS ------------------------------------------------------------------------------------------------------------------------------------	 
	protected ArrayList<cell> getListNeighbors ( typeNeighbourhood typeNeighbourhood , int cellX , int cellY ) {
		ArrayList<cell> list = new  ArrayList<cell> ();		
		switch (typeNeighbourhood) {
			case moore: {
				list.addAll(Arrays.asList(
						cells[checkCell(cellX+1,numCellX)][checkCell(cellY-1,numCellY)],
						cells[checkCell(cellX+1,numCellX)][checkCell(cellY,numCellY)],
						cells[checkCell(cellX+1,numCellX)][checkCell(cellY+1,numCellY)],
				
						cells[checkCell(cellX,numCellX)][checkCell(cellY+1,numCellY)],
						cells[checkCell(cellX,numCellX)][checkCell(cellY-1,numCellY)],
				
						cells[checkCell(cellX-1,numCellX)][checkCell(cellY-1,numCellY)],
						cells[checkCell(cellX-1,numCellX)][checkCell(cellY,numCellY)],
						cells[checkCell(cellX-1,numCellX)][checkCell(cellY+1,numCellY)]
								));		
			} break ;
		    	 
			case vonNewmann : {
				list.addAll(Arrays.asList(
						cells[checkCell(cellX,numCellX)][checkCell(cellY+1,numCellY)],
						cells[checkCell(cellX,numCellX)][checkCell(cellY-1,numCellY)],
						cells[checkCell(cellX-1,numCellX)][checkCell(cellY,numCellY)],
						cells[checkCell(cellX+1,numCellX)][checkCell(cellY,numCellY)]
								)); 
			} break ;	
			case m_vn : {
				list.addAll(Arrays.asList(		    			
						cells[checkCell(cellX+1,numCellX)][checkCell(cellY-1,numCellY)],				
						cells[checkCell(cellX+1,numCellX)][checkCell(cellY+1,numCellY)],							
						cells[checkCell(cellX-1,numCellX)][checkCell(cellY-1,numCellY)],
						cells[checkCell(cellX-1,numCellX)][checkCell(cellY+1,numCellY)]
								));		
			} break ;
		}
		return list; 
	 }	
	
	protected ArrayList<Double> getListValNeighbors ( typeNeighbourhood typeNeighbourhood , cell c ,int posVal ) {
		 ArrayList<Double> list = new  ArrayList<Double> ();
		int cellX = c.getX(), cellY = c.getY() ;
		switch (typeNeighbourhood) {
			case moore: list.addAll(Arrays.asList(
						cells[checkCell(cellX+1,numCellX)][checkCell(cellY-1,numCellY)].getVals()[posVal] ,
						cells[checkCell(cellX+1,numCellX)][checkCell(cellY,numCellY)].getVals()[posVal],
						cells[checkCell(cellX+1,numCellX)][checkCell(cellY+1,numCellY)].getVals()[posVal],
				
						cells[checkCell(cellX,numCellX)][checkCell(cellY+1,numCellY)].getVals()[posVal],
						cells[checkCell(cellX,numCellX)][checkCell(cellY-1,numCellY)].getVals()[posVal],
				
						cells[checkCell(cellX-1,numCellX)][checkCell(cellY-1,numCellY)].getVals()[posVal],
						cells[checkCell(cellX-1,numCellX)][checkCell(cellY,numCellY)].getVals()[posVal],
						cells[checkCell(cellX-1,numCellX)][checkCell(cellY+1,numCellY)].getVals()[posVal]
								));		
			break ;	 
			case vonNewmann : list.addAll(Arrays.asList(
						cells[checkCell(cellX,numCellX)][checkCell(cellY+1,numCellY)].getVals()[posVal],
						cells[checkCell(cellX,numCellX)][checkCell(cellY-1,numCellY)].getVals()[posVal],
						cells[checkCell(cellX-1,numCellX)][checkCell(cellY,numCellY)].getVals()[posVal],
						cells[checkCell(cellX+1,numCellX)][checkCell(cellY,numCellY)].getVals()[posVal]
								)); 
			break ;	
			case m_vn : list.addAll(Arrays.asList(		    			
						cells[checkCell(cellX+1,numCellX)][checkCell(cellY-1,numCellY)].getVals()[posVal],				
						cells[checkCell(cellX+1,numCellX)][checkCell(cellY+1,numCellY)].getVals()[posVal],							
						cells[checkCell(cellX-1,numCellX)][checkCell(cellY-1,numCellY)].getVals()[posVal],
						cells[checkCell(cellX-1,numCellX)][checkCell(cellY+1,numCellY)].getVals()[posVal]
								));		
			 break ; 
		}
		return list; 		
	}	
	
	protected ArrayList<cell> getListNeighbors ( typeNeighbourhood typeNeighbourhood , cell c ) {
		ArrayList<cell> list = new  ArrayList<cell> ();		
		int cellX = c.getX(), cellY = c.getY() ;
		switch (typeNeighbourhood) {
			case moore: {
				list.addAll(Arrays.asList(
						cells[checkCell(cellX+1,numCellX)][checkCell(cellY-1,numCellY)],
						cells[checkCell(cellX+1,numCellX)][checkCell(cellY,numCellY)],
						cells[checkCell(cellX+1,numCellX)][checkCell(cellY+1,numCellY)],
				
						cells[checkCell(cellX,numCellX)][checkCell(cellY+1,numCellY)],
						cells[checkCell(cellX,numCellX)][checkCell(cellY-1,numCellY)],
				
						cells[checkCell(cellX-1,numCellX)][checkCell(cellY-1,numCellY)],
						cells[checkCell(cellX-1,numCellX)][checkCell(cellY,numCellY)],
						cells[checkCell(cellX-1,numCellX)][checkCell(cellY+1,numCellY)]
								));		
			} break ;
		    	 
			case vonNewmann : {
				list.addAll(Arrays.asList(
						cells[checkCell(cellX,numCellX)][checkCell(cellY+1,numCellY)],
						cells[checkCell(cellX,numCellX)][checkCell(cellY-1,numCellY)],
						cells[checkCell(cellX-1,numCellX)][checkCell(cellY,numCellY)],
						cells[checkCell(cellX+1,numCellX)][checkCell(cellY,numCellY)]
								)); 
			} break ;	
			case m_vn : {
				list.addAll(Arrays.asList(		    			
						cells[checkCell(cellX+1,numCellX)][checkCell(cellY-1,numCellY)],				
						cells[checkCell(cellX+1,numCellX)][checkCell(cellY+1,numCellY)],							
						cells[checkCell(cellX-1,numCellX)][checkCell(cellY-1,numCellY)],
						cells[checkCell(cellX-1,numCellX)][checkCell(cellY+1,numCellY)]
								));		
			} break ;
		}
		return list; 
	 }	
	
	//check boundary condition
	private int checkCell ( int cell , int maxCell) {		
		if ( cell > maxCell - 1 ) 
			return 0  ;
		if ( cell  <= 0 ) 
			return maxCell-1;
		else return cell;
	}
	
// LIST CELL ACTIVE ---------------------------------------------------------------------------------------------------------------------------------	
	private void putCellInList (cell c) {
		if ( !listCell.contains(c))
			listCell.add(c);
	}
 
	public ArrayList<cell> getListCell () {
		return listCell;		
	}
	
	public cell getCell (int X , int Y) {
		try {
			return  cells[X][Y]; 
		} catch (ArrayIndexOutOfBoundsException e) {
			return null ;
		}
	} 
	
	public cell getCell ( double[] coords) {
		try {
			return cells[(int) Math.floor(coords[0])][(int) Math.floor(coords[1])] ;
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			return null ;
		}
	}
	
	public cell getCell ( Node n ) {
		double[] coords = GraphPosLengthUtils.nodePosition(n);	
		return cells[(int) Math.floor(coords[0])][(int) Math.floor(coords[1])] ;
	}

	public cell getCell ( bucket b ) {
		try {
			return  cells[b.getX()][b.getY()]; 
		} catch (ArrayIndexOutOfBoundsException e) {
			return null ;
		}
	}

	public cell getCell ( seed s ) {
		try {
			return cells[(int) Math.floor(s.getX() )][(int) Math.floor(s.getY() )] ;
		} catch (ArrayIndexOutOfBoundsException e) {
			return null ;
		}
	}
		
	public double getFeed ( ) {
		return f;
	}
	
	public double getKill ( ) {
		return k;
	}

	public int[] getSizeGrid () {
		return new int[] {numCellX, numCellY} ;
	}
	
	public cell[][] getCells () {
		return cells;
	}

	
}

