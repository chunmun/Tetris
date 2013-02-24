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
	public static final int N_PIECES = 7;
	
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
	
	/*
	 * Note that this method returns the fields with the complete rows INTACT
	 */
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
		
		// Determine baseline or insert height
		int height = 0; 
		for(int c = 0; c < pWidth[piece][orient]; c++){
			height = Math.max(height, fTop[slot+c] - pBottom[piece][orient][c]);
		}
		
		// Insert the piece
		for(int i = 0; i < pWidth[piece][orient]; i++){
			for(int j = height + pBottom[piece][orient][i]; j < height+pTop[piece][orient][i]; j++){
				suc_field[j][slot+i] = turn;
			}
		}
		return suc_field;
	}
	
	/*
	 * Note that this method returns the fields with the complete rows INTACT
	 */
	protected int[][] successorFieldStrict(int[][] field, int[] move, int turn, int piece){
		int[][] suc_field = new int[ROWS+4][COLS]; // Increased rows to accommodate for calculations at the border
		int[] top = generateFTop(field);
		
		// Deep clone
		for(int i = 0; i < field.length; i++){
			for(int j = 0; j < field[0].length; j++){
				suc_field[i][j] = field[i][j];
			}
		}
		
		int orient = move[ORIENT];
		int slot = move[SLOT];
		
		// Determine baseline or insert height
		int height = 0; 
		for(int c = 0; c < pWidth[piece][orient]; c++){
			height = Math.max(height, top[slot+c] - pBottom[piece][orient][c] + 1);
		}
		
		// Insert the piece
		for(int i = 0; i < pWidth[piece][orient]; i++){
			for(int j = height + pBottom[piece][orient][i]; j < height+pTop[piece][orient][i]; j++){
				if(j >= ROWS){
					break;
				}
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
	
	// Return a new field that has all its complete rows collapsed
	protected int[][] collapseField(int[][] field){
		int row = field.length;
		int col = field[0].length;
//		int[][] suc_field = cloneField(field);
		
		int full_rows[] = findCompleteRows(field);
		
		int rows_removed = 0;
		for(int i = 0; i < full_rows.length; i++){
			int row_ind = full_rows[i] - rows_removed;
			field = removeFRow(field,row_ind);
			rows_removed++;
		}
		
		return field;
	}
	
	// Deep clones the field
	protected int[][] cloneField(int[][] field){
		int row = field.length;
		int col = field[0].length;
		int[][] suc_field = new int[row][col];
		
		// Deep clone
		for(int i = 0; i < field.length; i++){
			for(int j = 0; j < field[0].length; j++){
				suc_field[i][j] = field[i][j];
			}
		}	
		return suc_field;
	}
	
	// Return the same field with a particular row removed and the columns collasped
	protected int[][] removeFRow(int[][] field, int r){
		int row = field.length;
		int col = field[0].length;
		
		for(int i = r; i < row - 1; i++){
			field[i] = field[i+1];
		}
		
		// Set the top row to 0
		for(int j = 0; j < col; j++){
			field[row-1][j] = 0;
		}
		
		return field;
	}
	
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
	
	// Returns the index of the inserted height on the field given this move
	protected int findInsertHeight(int[] move, int[][] field, int piece){
		int[] top = generateFTop(field);
		int orient = move[ORIENT];
		int slot = move[SLOT];
		
		int height = 0; 
		for(int c = 0; c < pWidth[piece][orient]; c++){
			height = Math.max(height, top[slot+c] - pBottom[piece][orient][c]);
		}
		
		return height;
	}
	
	protected int[][] removeExcess(int[][] x_field){
		int[][] suc_field = new int[ROWS][COLS];
		for(int i = 0; i < ROWS; i++){
			for(int j = 0; j < COLS; j++){
				suc_field[i][j] = x_field[i][j];
			}
		}
		return suc_field;
	}
	
	// Return an array of ROW indices in the field that have complete rows
	protected int[] findCompleteRows(int[][] field){
		Vector<Integer> found = new Vector<Integer>();
		for(int i=0; i < ROWS; i++){
			boolean full = true;
			
			for(int j=0; j < COLS; j++){
				if(field[i][j] == 0){
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
	
	// Returns an array of the col transitions in each COL in the field
	protected int[] findColTransitions(int[][] field){
		int[] col_trans = new int[COLS];
		
		// The bot exterior wall is implicitly filled
		// The top wall is unfilled
		for(int j = 0; j < COLS; j++){
			int count = 0;
			int last_cell = 1;
			
			for(int i = 0; i < ROWS; i++){
				
				if(field[i][j] > 0 && last_cell == 0){
					count++;
				}
				
				if(field[i][j] == 0 && last_cell > 0){
					count++;
				}
				
				last_cell = field[i][j];
			}
			
			if(last_cell == 1){
				count++;
			}
			
			col_trans[j] = count;
		}
		
		return col_trans;
	}
	
	// Returns an array of the row transitions in each ROW in the field
	protected int[] findRowTransitions(int[][] field){
		int[] row_trans = new int[ROWS];
		// The left and right exterior walls are implicitly filled
		for(int i = 0; i < ROWS; i++){
			int count = 0;
			int last_cell = 1;
			
			for(int j = 0; j < COLS; j++){
				
				if(field[i][j] > 0 && last_cell == 0){
					count++;
				}
				
				if(field[i][j] == 0 && last_cell > 0){
					count++;
				}
				
				last_cell = field[i][j];
			}
			
			if(last_cell == 0){
				count++;
			}
			
			row_trans[i] = count;
		}
		
		return row_trans;
	}
	
	// Returns an array of the depth of wells in each COL in the field
	protected int[] findWellsPerCol(int[][] field){
		int[] wells = new int[COLS];
		int[] top = generateFTop(field);
		
		for(int j = 0; j < COLS; j++){
			int leftTop = ROWS;
			if(j > 0){
				leftTop = top[j-1];
			}
			
			int rightTop = ROWS;
			if(j < COLS - 1){
				rightTop = top[j+1];
			}
			
			int low = Math.min(leftTop, rightTop);
			wells[j] = Math.max(0, low - top[j]);
		}
		
		return wells;
	}
	
	// Returns an array of the number of gaps + well cells in each COL in the field
	protected int[] findDeadGapsPerCol(int[][] field){
		int[] gaps = findGapsPerCol(field);
		int[] top = generateFTop(field);
		
		if(top[1] - top[0] > 2){
			gaps[0] += top[1] - top[0] - 2;
		}
		
		for(int i = 1; i < COLS - 1; i++){
			int diff = Math.min(top[i-1] - top[i], top[i+1] - top[i]);
			if(diff > 2){
				gaps[i] += diff - 2;
			}
		}
		
		if(top[COLS-2] - top[COLS-1] > 4){
			gaps[COLS-1] += top[COLS-2] - top[COLS-1] - 2;
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

	
	protected int sumGaps(int[] gaps){
		int g = 0;
		for(int j = 0; j < gaps.length; j++){
			g += gaps[j];
		}	
		return g;
	}
	
	protected int sumArray(int[] arr){
		int total = 0;
		for(int i = 0; i < arr.length; i++){
			total += arr[i];
		}
		return total;
	}
}

class Choice{
	public int g_value;
	public int h_value;
	public int f_value;
	public int move;
	
	public Choice(int move){
		g_value = 0;
		h_value = 0;
		f_value = 0;
		this.move = move;
	}
	
	public Choice(int move, int g_value, int h_value){
		this.move = move;
		this.g_value = g_value;
		this.h_value = h_value;
	}
	
	public String toString(){
		return "Choice "+move+" g: "+g_value+" f: "+f_value+" h: "+h_value;
	}
}