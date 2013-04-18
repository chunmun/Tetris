package blueBlox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

/**
 * Features used for the heuristics:
 * 1. Landing Height
 * 2. #cells removed due to completion
 * 3. #rows removed due to completion
 * 4. Eroded piece metric = feature2 * feature3
 * 5. Column Transition
 * 6. Row Transition
 * 7. Buried holes
 * 8. Wells
 * 
 * @author chunmun
 *
 */
public class PlayerGPDelleCherie implements IPlayer{
	// Initial coefficient of the weights in the combined heuristic score
	protected int cof_h = -1, cof_r = 1, cof_row = -1, cof_col = -1, cof_g = -6, cof_w = -2;

	public final int N_PIECES = 7;
	protected final int ROWS = 21;
	protected final int COLS = 10;

	//possible orientations for a given piece type
	protected final int[] pOrients = {1,2,4,4,4,2,2};

	//the next several arrays define the piece vocabulary in detail
	//width of the pieces [piece ID][orientation]
	protected final int[][] pWidth = {
			{2},
			{1,4},
			{2,3,2,3},
			{2,3,2,3},
			{2,3,2,3},
			{3,2},
			{3,2}
	};
	//height of the pieces [piece ID][orientation]
	protected final int[][] pHeight = {
			{2},
			{4,1},
			{3,2,3,2},
			{3,2,3,2},
			{3,2,3,2},
			{2,3},
			{2,3}
	};
	protected final int[][][] pBottom = {
			{{0,0}},
			{{0},{0,0,0,0}},
			{{0,0},{0,1,1},{2,0},{0,0,0}},
			{{0,0},{0,0,0},{0,2},{1,1,0}},
			{{0,1},{1,0,1},{1,0},{0,0,0}},
			{{0,0,1},{1,0}},
			{{1,0,0},{0,1}}
	};
	protected final int[][][] pTop = {
			{{2,2}},
			{{4},{1,1,1,1}},
			{{3,1},{2,2,2},{3,3},{1,1,2}},
			{{1,3},{2,1,1},{3,3},{2,2,2}},
			{{3,2},{2,2,2},{2,3},{1,2,1}},
			{{1,2,2},{3,2}},
			{{2,2,1},{2,3}}
	};

	protected final double[][] pFitScore = {
			{1.0/6.0},
			{1.0/9.0, 1.0/6.0},
			{1.0/6.0, 1.0/7.0, 1.0/8.0, 1.0/7.0},
			{1.0/6.0, 1.0/7.0, 1.0/8.0, 1.0/7.0},
			{1.0/7.0, 1.0/7.0, 1.0/7.0, 1.0/5.0},
			{1.0/6.0, 1.0/7.0},
			{1.0/6.0, 1.0/7.0},
	};

	protected final int ORIENT = 0;
	protected final int SLOT = 1;	// Index of legalMoves[piece]


	protected int[][] field;
	protected int[] fTop;
	protected int piece;

	public PlayerGPDelleCherie(){
		super();
	}

	@Override
	public int pickMove(State s, int[][] legalMoves) {
		// Load field if not loaded yet
		field = s.getField();
		fTop = s.getTop();
		piece = s.getNextPiece();

		int choice;
		try {
			choice = makeGreedyChoice(s,legalMoves);
			return choice;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	public PlayerGPDelleCherie(int[] cofs) throws Exception{
		super();
		if(cofs.length != 6){
			throw new Exception("GG lah, wrong number of coefficients");
		}

		this.cof_h = cofs[0];
		this.cof_r = cofs[1];
		this.cof_row = cofs[2];
		this.cof_col = cofs[3];
		this.cof_g = cofs[4];
		this.cof_w = cofs[5];
	}

	protected Vector<String> last_choice = new Vector<String>();

	public void printLastChoice(){
		for(String line : last_choice){
			System.out.println(line);
		}
	}


	public int makeGreedyChoice(State s, int[][] legalMoves) throws IOException{
		last_choice.clear();
		if(s.getRowsCleared() >= 1000000){
			// This is only used in the test to prevent it from running forever
			return 0;
		}
		int choice = 0;
		int score0 = Integer.MIN_VALUE;
		int[][] suc_field = cloneField(field);

		for(int i = 0; i < legalMoves.length; i++){
			suc_field = successorField(legalMoves[i], s.getTurnNumber() + 1, piece);

			// Landing Height
			int h_value = findInsertHeight(legalMoves[i], suc_field, piece);

			// Eroded Piece metric = #cells removed * #rows removed = #rows removed * COLS 
			int r_value = findCompleteRows(suc_field).length;

			// ==== IMPT We are removing the complete rows now! =====
			suc_field = collapseField(suc_field);

			// Column Transitions
			int[] column_trans = findColTransitions(suc_field);
			int col_value = sumArray(column_trans);

			// Row Transitions
			int[] row_trans = findRowTransitions(suc_field);
			int row_value = sumArray(row_trans);

			// Buried Holes
			int[] gaps = findGapsPerCol(suc_field);
			int g_value = sumArray(gaps);

			// Wells
			int[] wells = findWellsPerCol(suc_field);
			int w_value = sumArray(wells);


			// Compute a weighted sum
			//			System.out.println("Move: "+Arrays.toString(legalMoves[i]));
			//			System.out.println("Score_h : "+h_value);
			//			System.out.println("Score_r : "+r_value);
			//			System.out.println("Score_row : "+row_value);
			//			System.out.println("Score_col : "+col_value);
			//			System.out.println("Score_g : "+g_value);
			//			System.out.println("Score_w : "+w_value);
			//			FileWriter f = new FileWriter(new File("DCoutput.txt"), true);
			//			f.write("move:"+Arrays.toString(legalMoves[i])+"["+h_value+","+r_value+","+row_value+","+col_value+","+g_value+","+w_value+"]"+System.getProperty("line.separator"));
			//			f.close();
			//			System.out.println("Score fit : "+score_fit);


			int score = 0;
			score += cof_h * h_value;
			score += cof_r * r_value;
			score += cof_row * row_value;
			score += cof_col * col_value;
			score += cof_g * g_value;
			score += cof_w * w_value;

			if(findMaxHeight(suc_field) >= ROWS-1){
				score = Integer.MIN_VALUE;
			}
			last_choice.add("move:"+Arrays.toString(legalMoves[i])+"["+h_value+","+r_value+","+row_value+","+col_value+","+g_value+","+w_value+"] => "+score);

			if(score > score0){
				//					System.out.println("Switching Choice =============== " + score0 +"=>"+score);
				last_choice.add("Switching Choice ========= " + score0 +" => "+score);
				score0 = score;
				choice = i;
			}
		}

		return choice;
	}



	public static void main(String args[]){

		//		State s = new State();
		//		new TFrame(s);
		//		PlayerGPDelleCherie p = new PlayerGPDelleCherie();
		//		long last_time = System.currentTimeMillis();
		//		Random r = new Random();
		//		while(!s.hasLost()) {
		//			s.setNextPiece(Math.abs(r.nextInt())%7);
		//			s.makeMove(p.pickMove(s,s.legalMoves()));
		//			if((System.currentTimeMillis() - last_time) > 10000){
		//				last_time = System.currentTimeMillis();
		//			s.draw();
		//			s.drawNext(0,0);
		//			try {
		//				Thread.sleep(1);
		//			} catch (InterruptedException e) {
		//				e.printStackTrace();
		//			}	
		//			}
		//	}
		//		s.draw();
		//		s.drawNext(0,0);
		//		System.out.println("You have completed "+s.getRowsCleared()+" rows.");

		
		// ================= Testing code ====================
		
		int numRuns = 1;
		ArrayList<Integer> total = new ArrayList<Integer>();
		long sum = 0;
		for(int i = 0; i < numRuns; i++){
			State s = new State();
			PlayerGPDelleCherie p = new PlayerGPDelleCherie();
			
			System.out.println(">>> Start Run "+i);
			while(!s.hasLost()){
				s.makeMove(p.pickMove(s, s.legalMoves()));
			}
			System.out.println("<<< Finish Run "+i);
			
			total.add(s.getRowsCleared());
			sum += s.getRowsCleared();
		}
		
		double avg = (double)sum / (double)numRuns;
		double sq_total = 0;
		
		for(int i : total) {
			sq_total += Math.pow(i-avg, 2);
		}
		
		double var = sq_total / (double)numRuns;
		System.out.println("Mean : "+avg);
		System.out.println("Variance: "+var);
		System.out.println(total.toString());
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

	// Returns the max ROW index of the field
	protected int findMaxHeight(int[][] field){
		int height = 0;
		int[] top = generateFTop(field);
		for(int i = 0; i < top.length; i++){
			height = Math.max(height, top[i]);
		}
		return height;
	}


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

	protected int sumArray(int[] arr){
		int total = 0;
		for(int i = 0; i < arr.length; i++){
			total += arr[i];
		}
		return total;
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
}