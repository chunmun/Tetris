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
	@Override
	public int makeGreedyChoice(State s, int[][] legalMoves){
		int choice = 0;
		int score0 = Integer.MIN_VALUE;
		int[][] suc_field = cloneField(field);
		
		for(int i = 0; i < legalMoves.length; i++){
			suc_field = successorField(legalMoves[i], s.getTurnNumber() + 1, piece);
		
			// Landing Height
			int h_value = findInsertHeight(legalMoves[i], suc_field);
			
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
			score += -1 * h_value;
			score += 1 * r_value;
			score += -1 * row_value;
			score += -1 * col_value;
			score += -5 * g_value;
			score += -1 * w_value;
			
			if(score > score0){
				score0 = score;
				choice = i;
			}
		}
		
		return choice;
	}
	

	
	public static void main(String args[]){
		
		State s = new State();
//		new TFrame(s);
		PlayerGPDelleCherie p = new PlayerGPDelleCherie();
		while(!s.hasLost()) {
			s.makeMove(p.pickMove(s,s.legalMoves()));
//			s.draw();
//			s.drawNext(0,0);
//			try {
//				Thread.sleep(5);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		}
		System.out.println("You have completed "+s.getRowsCleared()+" rows.");
	}	
}