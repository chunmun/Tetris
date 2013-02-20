package blueBlox;

import java.util.Vector;

/**
 * Superclass of Greedy Players
 * Meant to be extended and makeGreedyChoice should be overwritten with a Greedy Algo
 * 
 * @author chunmun
 *
 */
public class PlayerGreed implements IPlayer {
	protected int[] pOrient;		// #orients per piece
	protected int[][] pWidth;		// [piece][orient] width
	protected int[][] pHeight;	// [piece][orient] max height
	protected int[][][] pBottom;	// [piece][orient][slots]
	protected int[][][] pTop;	// [piece][orient][slots]
	
	protected int piece;
	
	protected int[][] field;
	protected int[] fTop;
	protected int ROWS;
	protected int COLS;
	
	protected int ORIENT = 0;
	protected int SLOT = 1;	// Index of legalMoves[piece]
	
	protected boolean init = false;
	
	@Override
	public int pickMove(State s, int[][] legalMoves) {
		// Load field if not loaded yet
		field = s.getField();
		fTop = s.getTop();
		piece = s.getNextPiece();
		loadAttributes(s);
		
		int choice = makeGreedyChoice(s,legalMoves);

		return choice;
	}
	
	protected int makeGreedyChoice(State s, int[][] legalMoves){

		return 0; // Placeholder code
	}
	
	protected void loadAttributes(State s){
		if(!init){
			pOrient = s.getpOrients();
			pWidth = s.getpWidth();
			pHeight = s.getpHeight();
			pBottom = s.getpBottom();
			pTop = s.getpTop();
			
			ROWS = field.length;
			COLS = field[0].length;
			
			init = true;
		}else{
			return;
		}
	}
	
	protected int[][] successorField(int[] move, int turn, int piece){
		int[][] suc_field = new int[ROWS+4][COLS]; // Increased rows to accommodate for calculations at the border
		
		// Deep clone
		for(int i = 0; i < field.length; i++){
			for(int j = 0; j < field[0].length; j++){
				suc_field[i][j] = field[i][j];
			}
		}
		
		int orient = move[ORIENT];
		int slot = move[SLOT];
		
		int height = 0; 
		for(int c = 0; c < pWidth[piece][orient]; c++){
			height = Math.max(height, fTop[slot+c] - pBottom[piece][orient][c]);
		}
		
		for(int i = 0; i < pWidth[piece][orient]; i++){
			for(int j = height + pBottom[piece][orient][i]; j < height+pTop[piece][orient][i]; j++){
				suc_field[j][slot+i] = turn;
			}
		}
		
		return suc_field;
	}
		
	protected void printField(int[][] field){
		System.out.println("{");
		for(int i = 0; i < field.length; i++){
			System.out.print("[");
			for(int j = 0; j < field[0].length; j++){
				System.out.print(field[i][j]+",");
			}
			System.out.println("]");
		}
		System.out.println("}");
	}
	
	// ===== General Field Methods =====
	
	// Returns an array containing the max ROW in each COL of the field
	protected int[] generateFTop(int[][] field){
		int new_top[] = new int[COLS];
		for(int i = 0; i < COLS; i++){
			for(int j = 0; j < ROWS; j++){
				if(field[j][i] != 0){
					new_top[i] = j;
					continue;
				}
			}
		}
		return new_top;
	}
	
	// Returns the max ROW index of the field
	protected int findMaxHeight(int[][] field){
		int height = 0;
		int[] top = generateFTop(field);
		for(int i = 0; i < top.length; i++){
			height = Math.max(height, top[i]);
		}
		return height;
	}	
	
	// Return an array of ROW indices in the field that have complete rows
	protected int[] findCompleteRows(int[][] field){
		Vector<Integer> found = new Vector<Integer>();
		for(int i=0; i < ROWS; i++){
			boolean full = true;
			
			for(int j=0; j < COLS; j++){
				if(field[i][j] != 0){
					full = false;
					break;
				}
			}
			
			if(full){
				found.add(i);
			}
		}
	
		// Convert Vector back to int[]
		int[] found_arr = new int[found.size()];
		for(int i = 0; i < found.size(); i++){
			found_arr[i] = found.get(i);
		}
		return found_arr;
	}
	
	// Returns an array of the number of gaps in each COL in the field
	protected int[] findGapsPerCol(int[][] field){
		int[] gaps = new int[COLS];
		int[] top = generateFTop(field);
		
		for(int i = 0; i < COLS; i++){
			for(int j = 0; j < top[i]; j++){
				if(field[j][i] == 0){
					gaps[i]++;
				}
			}
		}
		
		return gaps;
	}
	
	protected int[] findDeadGapsPerCol(int[][] field){
		int[] gaps = findGapsPerCol(field);
		int[] top = generateFTop(field);
		
		if(top[1] - top[0] > 4){
			gaps[0] += top[1] - top[0] - 4;
		}
		
		for(int i = 1; i < COLS - 1; i++){
			int diff = Math.min(top[i-1] - top[i], top[i+1] - top[i]);
			if(diff > 4){
				gaps[i] += diff - 4;
			}
		}
		
		if(top[COLS-2] - top[COLS-1] > 4){
			gaps[COLS-1] += top[COLS-2] - top[COLS-1] - 4;
		}
		
		return gaps;
	}
	
	// Returns the sum of the absolute difference in height between each adjacent column
	protected int getTopDiff(int[] top){
		int d = 0;
		for(int i = 0; i < top.length-1; i++){
			d += Math.abs(top[i+1] - top[i]);
		}
		return d;
	}
	
	// Returns the sum of the square of difference in height between each adj column
	protected int getTopDiffSq(int[] top){
		int d = 0;
		for(int i = 0; i < top.length-1; i++){
			d += Math.pow(top[i+1] - top[i], 2);
		}
		return d;
	}
}
