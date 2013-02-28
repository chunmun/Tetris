package blueBlox;

import java.util.Collections;
import java.util.Comparator;
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
public class PlayerGPDelleCherie extends PlayerGreed implements IPlayer{
	// Initial coefficient of the weights in the combined heuristic score
	protected int cof_h = -1, cof_r = 1, cof_row = -1, cof_col = -1, cof_g = -6, cof_w = -2;

	public PlayerGPDelleCherie(){
		super();
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

	@Override
	public int makeGreedyChoice(State s, int[][] legalMoves){
		if(s.getRowsCleared() >= 100000){
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

			if(score > score0){
				score0 = score;
				choice = i;
			}
		}

		return choice;
	}



	public static void main(String args[]){

		State s = new State();
		new TFrame(s);
		PlayerGPDelleCherie p = new PlayerGPDelleCherie();
//		long last_time = System.currentTimeMillis();
		while(!s.hasLost()) {
			s.makeMove(p.pickMove(s,s.legalMoves()));
//			if((System.currentTimeMillis() - last_time) > 10000){
//				last_time = System.currentTimeMillis();
				s.draw();
				s.drawNext(0,0);
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}	
//			}
		}
		s.draw();
		s.drawNext(0,0);
		System.out.println("You have completed "+s.getRowsCleared()+" rows.");
	}	


}