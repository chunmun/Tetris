package blueBlox;

import java.util.Random;
import java.util.Vector;

/**
 * @author chunmun
 *
 */
public class PDelleFast implements IPlayer {
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

	protected final int[][][] legalMoves = {
			{{0,0},{0,1},{0,2},{0,3},{0,4},{0,5},{0,6},{0,7},{0,8}},
			{{0,0},{0,1},{0,2},{0,3},{0,4},{0,5},{0,6},{0,7},{0,8},{0,9},{1,0},{1,1},{1,2},{1,3},{1,4},{1,5},{1,6}},
			{{0,0},{0,1},{0,2},{0,3},{0,4},{0,5},{0,6},{0,7},{0,8},{1,0},{1,1},{1,2},{1,3},{1,4},{1,5},{1,6},{1,7},{2,0},{2,1},{2,2},{2,3},{2,4},{2,5},{2,6},{2,7},{2,8},{3,0},{3,1},{3,2},{3,3},{3,4},{3,5},{3,6},{3,7}},
			{{0,0},{0,1},{0,2},{0,3},{0,4},{0,5},{0,6},{0,7},{0,8},{1,0},{1,1},{1,2},{1,3},{1,4},{1,5},{1,6},{1,7},{2,0},{2,1},{2,2},{2,3},{2,4},{2,5},{2,6},{2,7},{2,8},{3,0},{3,1},{3,2},{3,3},{3,4},{3,5},{3,6},{3,7}},
			{{0,0},{0,1},{0,2},{0,3},{0,4},{0,5},{0,6},{0,7},{0,8},{1,0},{1,1},{1,2},{1,3},{1,4},{1,5},{1,6},{1,7},{2,0},{2,1},{2,2},{2,3},{2,4},{2,5},{2,6},{2,7},{2,8},{3,0},{3,1},{3,2},{3,3},{3,4},{3,5},{3,6},{3,7}},
			{{0,0},{0,1},{0,2},{0,3},{0,4},{0,5},{0,6},{0,7},{1,0},{1,1},{1,2},{1,3},{1,4},{1,5},{1,6},{1,7},{1,8}},
			{{0,0},{0,1},{0,2},{0,3},{0,4},{0,5},{0,6},{0,7},{1,0},{1,1},{1,2},{1,3},{1,4},{1,5},{1,6},{1,7},{1,8}},
	};

	protected int[][] sucField1 = {
			{0,0,0,0,0,0,0,0,0,0}, {0,0,0,0,0,0,0,0,0,0}, {0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0}, {0,0,0,0,0,0,0,0,0,0}, {0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0}, {0,0,0,0,0,0,0,0,0,0}, {0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0}, {0,0,0,0,0,0,0,0,0,0}, {0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0}, {0,0,0,0,0,0,0,0,0,0}, {0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0}, {0,0,0,0,0,0,0,0,0,0}, {0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0}, {0,0,0,0,0,0,0,0,0,0}, {0,0,0,0,0,0,0,0,0,0}
	};

	protected int[][] sucField2 = {
			{0,0,0,0,0,0,0,0,0,0}, {0,0,0,0,0,0,0,0,0,0}, {0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0}, {0,0,0,0,0,0,0,0,0,0}, {0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0}, {0,0,0,0,0,0,0,0,0,0}, {0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0}, {0,0,0,0,0,0,0,0,0,0}, {0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0}, {0,0,0,0,0,0,0,0,0,0}, {0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0}, {0,0,0,0,0,0,0,0,0,0}, {0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0}, {0,0,0,0,0,0,0,0,0,0}, {0,0,0,0,0,0,0,0,0,0}
	};

	protected int[][] field = {
			{0,0,0,0,0,0,0,0,0,0}, {0,0,0,0,0,0,0,0,0,0}, {0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0}, {0,0,0,0,0,0,0,0,0,0}, {0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0}, {0,0,0,0,0,0,0,0,0,0}, {0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0}, {0,0,0,0,0,0,0,0,0,0}, {0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0}, {0,0,0,0,0,0,0,0,0,0}, {0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0}, {0,0,0,0,0,0,0,0,0,0}, {0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0}, {0,0,0,0,0,0,0,0,0,0}, {0,0,0,0,0,0,0,0,0,0}
	};

	protected int[] fTop = {0,0,0,0,0,0,0,0,0,0};
	protected int[] suc1Top = {0,0,0,0,0,0,0,0,0,0};

	protected int turn = 0;
	protected int curPiece;
	protected int nextPiece;
	protected int move;

	protected double scoreCurrentMove = -999999999;
	protected double scoreNextMove = -999999999;
	protected int choiceCurrentMove = 0;
	protected int choiceNextMove = 0;

	protected int moveOrient;
	protected int moveSlot;
	protected int insert_height;

	// Initial coefficient of the weights in the combined heuristic score
	// They are all positive, the sign changing only occurs when adding up the scores
	protected double cof_h = -1, cof_r = 1, cof_row = -1, cof_col = -1, cof_g = -6, cof_w = -2, cof_fit = 0;
	//	protected double cof_h = -4.500158825082766, cof_r = 3.4181268101392694, cof_row = -3.2178882868487753, cof_col = -9.348695305445199, cof_g = -7.899265427351652, cof_w = -3.3855972247263626, cof_fit = 0;
	protected int score_h = 0;
	protected int score_r = 0;
	protected int score_row = 0;
	protected int score_col = 0;
	protected int score_g = 0;
	protected int score_w = 0;
	protected int score_fit = 0;

	protected boolean sucField1GG = false;

	protected Vector<String> last_choice = new Vector<String>();
	protected Vector<String> last_g = new Vector<String>();

	public PDelleFast(){
	}

	public PDelleFast(double vector[]){
		cof_h = vector[0];
		cof_r = vector[1];
		cof_row = vector[2];
		cof_col = vector[3];
		cof_g = vector[4];
		cof_w = vector[5];
		cof_fit = vector[6];
	}

	public void printLastChoice(){
		for(String line : last_choice){
			System.out.println(line);
		}
		System.out.println("coeffients, cof_h:"+cof_h+",cof_r:"+cof_r+",cof_row:"+cof_row+",cof_col:"+cof_col+",cof_g:"+cof_g+",cof_w:"+cof_w);
	}

	public void printLastG(){
		for(String line : last_g){
			System.out.println(line);
		}
	}
	@Override
	public int pickMove(State s, int[][] l) {
		//		last_choice.clear();
		//		last_g.clear();
		if(s.getRowsCleared() > 10000000){
			return 0;
		}
		cloneField(s.getField(),field);
		fTop = s.getTop();
		curPiece = s.getNextPiece();

		choiceCurrentMove = 0;
		scoreCurrentMove = -999999999;
		//		last_g.add("==== Top before anything : " + Arrays.toString(generateFTop(field)));
		for(move = 0; move < legalMoves[curPiece].length; move++){
			//			System.out.println("MOVE "+ move);
			cloneField(field, sucField1);
			// score_fit is computed in the successorField call
			successorField(legalMoves[curPiece][move], curPiece, turn, sucField1);
			suc1Top = generateFTop(sucField1);
			////			last_g.add("The top by this move before collapse: "+Arrays.toString(generateFTop(sucField1)));
			//			for(int[] row : sucField1){
			//				last_g.add(Arrays.toString(row));
			//			}
			score_h = findInsertHeight(legalMoves[curPiece][move], curPiece);

			// score_r is updated in the collapseField function
			collapseSucField1();
			suc1Top = generateFTop(sucField1);
			//			last_g.add("The top by this move after collapse: "+Arrays.toString(generateFTop(sucField1)));
			//			for(int[] row : sucField1){
			//				last_g.add(Arrays.toString(row));
			//			}
			computeScoreSucField1(move);

			if(!sucField1GG){
				// Compute the total score and check against scoreCurrentMove
				double score = 0;
				score += cof_h * score_h;
				score += cof_r * score_r;
				score += cof_row * score_row;
				score += cof_col * score_col;
				score += cof_g * score_g;
				score += cof_w * score_w;
				score += cof_fit * score_fit;

				//				printField(sucField1);
				//				System.out.println("Move: "+Arrays.toString(legalMoves[curPiece][move]));
				//				System.out.println("Score_h : "+score_h);
				//				System.out.println("Score_r : "+score_r);
				//				System.out.println("Score_row : "+score_row);
				//				System.out.println("Score_col : "+score_col);
				//				System.out.println("Score_g : "+score_g);
				//				System.out.println("Score_w : "+score_w);
				//
				//				try{
				//
				//					FileWriter f = new FileWriter(new File("DFoutput.txt"), true);
				//					f.write("move:"+Arrays.toString(legalMoves[curPiece][move])+"["+score_h+","+score_r+","+score_row+","+score_col+","+score_g+","+score_w+"]"+System.getProperty("line.separator"));
				//					f.close();
				//
				//				}catch(IOException e){
				//
				//				}

				//				last_choice.add("move:"+Arrays.toString(legalMoves[curPiece][move])+"["+score_h+","+score_r+","+score_row+","+score_col+","+score_g+","+score_w+"] => "+score);
				//				System.out.println("Score fit : "+score_fit);
				if(score > scoreCurrentMove){
					//					System.out.println("Switching Choice =============== " + scoreCurrentMove +"=>"+score);
					//					last_choice.add("Switching Choice ========= " + scoreCurrentMove +" => "+score);
					scoreCurrentMove = score;
					choiceCurrentMove = move;
				}
			} 
		}

		turn++;
		return choiceCurrentMove;
	}

	protected int[] zeroRow = {0,0,0,0,0,0,0,0,0,0};
	protected void collapseSucField1(){
		boolean complete = true;
		int gap = 0;
		score_r = 0;

		for(int row = 0; row < ROWS; row++){
			complete = true;


			for(int col = 0; col < COLS; col++){
				if(sucField1[row][col] == 0){
					complete = false;
					break;
				}
			}

			if(complete){
				score_r++;
				gap++;
				System.arraycopy(zeroRow,0,sucField1[row],0,COLS);
			} else {
				if(gap > 0){
					System.arraycopy(sucField1[row],0,sucField1[row-gap],0,COLS);
					System.arraycopy(zeroRow,0,sucField1[row],0,COLS);
				}			
			}
		}
	}

	protected final int[] rowTransOrig = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
	protected int[] rowTrans = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};

	protected void computeScoreSucField1(int move){
		boolean isTop = false;
		score_col = 0;
		score_row = 0;
		score_g = 0;
		score_w = 0;
		sucField1GG = false;

		System.arraycopy(rowTransOrig,0,rowTrans,0,ROWS);

		for(int col = 0; col < COLS; col++){
			isTop = false;

			for(int row = ROWS - 1; row >= 0; row--){

				// Row Transition
				if((sucField1[row][col] == 0 && rowTrans[row] > 0) || (sucField1[row][col] > 0 && rowTrans[row] == 0)){
					score_row++;
					rowTrans[row] = sucField1[row][col];

				}

				// Right Edge check
				if(col == COLS - 1 && sucField1[row][col] == 0){
					score_row++;
				}

				// Col Transition
				if(row == ROWS-1){
					if(sucField1[row][col] > 0){
						//						printField(sucField1);
						sucField1GG = true;
						score_col++;
						return;
					}
				} else if((sucField1[row][col] == 0 && sucField1[row+1][col] > 0) || (sucField1[row][col] > 0 && sucField1[row+1][col] == 0)){
					score_col++;
				}

				// Bottom Edge Check
				if(row == 0){
					if(sucField1[row][col] == 0)
						score_col++;
				}

				// Gaps 
				if(row < suc1Top[col] && sucField1[row][col] == 0){
					score_g++;
					//					last_g.add("move : "+Arrays.toString(legalMoves[curPiece][move])+" Adding "+score_g+" @ row : "+row+", col : "+col+" and the top is represented by "+Arrays.toString(suc1Top));
				}
			}
		}

		for(int j = 0; j < COLS; j++){
			int leftTop = ROWS;
			if(j > 0){
				leftTop = suc1Top[j-1];
			}

			int rightTop = ROWS;
			if(j < COLS - 1){
				rightTop = suc1Top[j+1];
			}

			int low = Math.min(leftTop, rightTop);
			score_w += Math.max(0, low - suc1Top[j]);
		}
		//		System.out.println("Top : "+Arrays.toString(suc1Top));
	}


	protected void successorField(int[] move, int piece, int turn, int[][] sucField){
		moveOrient = move[ORIENT];
		moveSlot = move[SLOT];

		score_fit = 0;
		insert_height = 0;
		score_h = 0;

		// Determine baseline or insert height
		for(int c = 0; c < pWidth[piece][moveOrient]; c++){
			insert_height = Math.max(insert_height, fTop[moveSlot+c] - pBottom[piece][moveOrient][c]);
		}

		// Insert the piece
		for(int i = 0; i < pWidth[piece][moveOrient]; i++){
			for(int j = insert_height + pBottom[piece][moveOrient][i]; j < Math.min(insert_height+pTop[piece][moveOrient][i], ROWS); j++){
				sucField[j][moveSlot+i] = 1;
				int rowPos = j;
				int colPos = moveSlot+i;
				
				if(colPos - 1 < 0 || sucField[rowPos][colPos-1] > 0)
					score_fit++;
				if(colPos + 1 >= State.COLS || sucField[rowPos][colPos+1] > 0)
					score_fit++;
				if(rowPos - 1 < 0 || sucField[rowPos-1][colPos] > 0) {
					score_fit++;
				}
				// Check bottom touching
				/*if(j == insert_height + pBottom[piece][moveOrient][i]){
					if(j > 0){
						if(sucField[j-1][moveSlot+i] > 0){
							score_fit += pFitScore[piece][moveOrient];
						}
					} else {
						score_fit += pFitScore[piece][moveOrient];
					}
				}
				score_h = Math.max(score_h, j);

				// Check left touching
				if(i == 0){
					if(moveSlot+i > 0){
						if(sucField[j][moveSlot+i-1] > 0){
							score_fit += pFitScore[piece][moveOrient];
						}
					} else {
						score_fit += pFitScore[piece][moveOrient];
					}
				}

				// Check right touching
				if(i == pWidth[piece][moveOrient] - 1){
					if(moveSlot+i < COLS - 1){
						if(sucField[j][moveSlot+i+1] > 0){
							score_fit += pFitScore[piece][moveOrient];
						}
					} else {
						score_fit += pFitScore[piece][moveOrient];
					}
				}*/
			}
		}
	}

	protected final void cloneField(int[][] f1, int[][] f2){
		for(int i = 0; i < ROWS; i++){
			System.arraycopy(f1[i],0,f2[i],0,COLS);
		}
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

	// Returns the index of the inserted height on the field given this move
	protected int findInsertHeight(int[] move, int piece){
		int orient = move[ORIENT];
		int slot = move[SLOT];

		int height = 0;
		for(int c = 0; c < pWidth[piece][orient]; c++){
			height = Math.max(height, suc1Top[slot+c] - pBottom[piece][orient][c]);
		}

		return height;
	}	

	public static void main(String args[]){
				State s = new State();
//				new TFrame(s);
		//		PDelleFast p = new PDelleFast(new double[] {-1,1,-1,-1,-6,-2,0});
				PDelleFast p = new PDelleFast();
				Random r = new Random(1);
				while(!s.hasLost()) {
					s.setNextPiece(Math.abs(r.nextInt())%7);
					s.makeMove(p.pickMove(s,s.legalMoves()));
		//			s.draw();
		//			s.drawNext(0,0);
		//			try {
		//				Thread.sleep(1);
		//			} catch (InterruptedException e) {
		//				e.printStackTrace();
		//			}	
				}
//				s.draw();
//				s.drawNext(0,0);
				System.out.println("You have completed "+s.getRowsCleared()+" rows.");
//		int numRuns = 100;
//		ArrayList<Integer> total = new ArrayList<Integer>();
////		long sum = 0;
//		//for(int k = 0; k <= 10; k++) {
//			total.clear();
			
//			System.out.println(">>> Start Run "+k);
//			for(int i = 0; i < numRuns; i++){
//				State s = new State();
//				PDelleFast p = new PDelleFast(new double[] {-1,1,-1,-1,-6,-2,0.25});
//
//				while(!s.hasLost()){
//					s.makeMove(p.pickMove(s, s.legalMoves()));
//				}
//
//				total.add(s.getRowsCleared());
//				System.out.println(s.getRowsCleared());
////				sum += s.getRowsCleared();
//
//			}
////
////			double avg = (double)sum / (double)numRuns;
////			double sq_total = 0;
////
////			for(int j : total) {
////				sq_total += Math.pow(j-avg, 2);
////			}
////
////			double var = sq_total / (double)numRuns;
////			System.out.println("Mean : "+avg);
////			System.out.println("Variance: "+var);
//			String res = total.toString();
//			System.out.println(res.substring(1,res.length()-1));
////			System.out.println("<<< Finish Run "+k);
//		//}
//		System.out.println("DONE");


	}

	public double runTest(){
		State s = new State();
		while(!s.hasLost()) {
			s.makeMove(this.pickMove(s,s.legalMoves()));
			if(s.getRowsCleared()>10000000){
				return 10000000;
			}
		}
		int res = s.getRowsCleared();
		return res;
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

}
