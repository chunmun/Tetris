package blueBlox;

import java.util.Random;


public class PlayerGPDCForward extends PlayerGreed implements IPlayer{

	// Initial coefficient of the weights in the combined heuristic score
	protected int cof_h = -1, cof_r = 1, cof_row = -1, cof_col = -1, cof_g = -6, cof_w = -2;

	public PlayerGPDCForward(){
		super();
	}

	public PlayerGPDCForward(int[] cofs) throws Exception{
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

	@Override
	public int makeGreedyChoice(State s, int[][] legalMoves){
		if(s.getRowsCleared() >= 100000){
			// This is only used in the test to prevent it from running forever
			return 0;
		}

		/*double[] piece_distri = new double[7];
		int sum = 0;
		Random rd = new Random();
		for(int i=0; i<7; i++){
			int k = rd.nextInt(7)+1;
			piece_distri[i] = k;
			sum += k;
		}
		for(int i=0; i<7; i++){
			piece_distri[i] = piece_distri[i]/sum;
		}*/


		double[] piece_distri = {0.1428, 0.1428, 0.1428, 0.1428, 0.1428, 0.1428, 0.1428};
		int scoreFinal = Integer.MIN_VALUE;;
		int choiceFinal = 0;

		for(int k = 0; k < legalMoves.length; k++){
			int avg_score = 0;
			int[][] suc_field = successorField(legalMoves[k], s.getTurnNumber() + 1, piece);
			int[][] cloned_suc = cloneField(suc_field);

			// Landing Height
			int h_value0 = findInsertHeight(legalMoves[k], cloned_suc, piece);

			// Eroded Piece metric = #cells removed * #rows removed = #rows removed * COLS 
			int r_value0 = findCompleteRows(cloned_suc).length;

			// ==== IMPT We are removing the complete rows now! =====
			cloned_suc = collapseField(cloned_suc);

			// Column Transitions
			int[] column_trans0 = findColTransitions(cloned_suc);
			int col_value0 = sumArray(column_trans0);

			// Row Transitions
			int[] row_trans0 = findRowTransitions(cloned_suc);
			int row_value0 = sumArray(row_trans0);

			// Buried Holes
			int[] gaps0 = findGapsPerCol(cloned_suc);
			int g_value0 = sumArray(gaps0);

			// Wells
			int[] wells0 = findWellsPerCol(suc_field);
			int w_value0 = sumArray(wells0);


			// Compute a weighted sum
			int score00 = 0;
			score00 += cof_h * h_value0;
			score00 += cof_r * r_value0;
			score00 += cof_row * row_value0;
			score00 += cof_col * col_value0;
			score00 += cof_g * g_value0;
			score00 += cof_w * w_value0;

			for(int j=0; j<N_PIECES; j++){
				int nextPiece = j;

				int choice = 0;
				int score0 = Integer.MIN_VALUE;
				int score1 = Integer.MAX_VALUE;
				int[][] next_field = cloneField(suc_field);
				int[][] legalMoves2 = s.legalMoves(j);

				for(int i = 0; i < legalMoves2.length; i++){
					next_field = successorFieldStrict(suc_field, legalMoves2[i], s.getTurnNumber() + 1, nextPiece);

					// Landing Height
					int h_value = findInsertHeight(legalMoves2[i], next_field, nextPiece);

					// Eroded Piece metric = #cells removed * #rows removed = #rows removed * COLS 
					int r_value = findCompleteRows(next_field).length;

					// ==== IMPT We are removing the complete rows now! =====
					next_field = collapseField(next_field);

					// Column Transitions
					int[] column_trans = findColTransitions(next_field);
					int col_value = sumArray(column_trans);

					// Row Transitions
					int[] row_trans = findRowTransitions(next_field);
					int row_value = sumArray(row_trans);

					// Buried Holes
					int[] gaps = findGapsPerCol(next_field);
					int g_value = sumArray(gaps);

					// Wells
					int[] wells = findWellsPerCol(next_field);
					int w_value = sumArray(wells);


					// Compute a weighted sum
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
					//System.out.println(score);
					if(score > score0){
						score0 = score;
						choice = i;
					}
					if(score < score1){
						score1 = score;
					}
				}

				avg_score += piece_distri[j]*score0;
			}
			//System.out.println(avg_score);
			int score = 9*score00 + 1*avg_score;
			if(score > scoreFinal){
				scoreFinal = score;
				choiceFinal = k;
			}
		}
		//System.out.println(choiceFinal);
		return choiceFinal;
	}

	public static void main(String args[]){

		State s = new State();
		new TFrame(s);
		PlayerGPDCForward p = new PlayerGPDCForward();
//		long last_time = System.currentTimeMillis();

		//s.setNextPiece(piece);
		while(!s.hasLost()) {
			s.makeMove(p.pickMove(s,s.legalMoves()));
			//piece = 3;
//			if(System.currentTimeMillis() - last_time > 10000){
				s.draw();
				s.drawNext(0,0);
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}	
//				System.out.println("Rows Cleared: "+s.getRowsCleared());
//				last_time = System.currentTimeMillis();
//			}
			//}
		}
		s.draw();
		s.drawNext(0,0);
		System.out.println("You have completed "+s.getRowsCleared()+" rows.");
	}	
}
