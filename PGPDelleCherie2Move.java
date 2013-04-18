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
public class PGPDelleCherie2Move extends PlayerGreed implements IPlayer{
	// Initial coefficient of the weights in the combined heuristic score
//	protected int cof_h = -1, cof_r = 1, cof_row = -1, cof_col = -1, cof_g = -5, cof_w = -1;
//[0, 1, -8, -9, -8, -7]
	protected int cof_h = 0, cof_r = 1, cof_row = -8, cof_col = -9, cof_g = -8, cof_w = -7;
	
	public PGPDelleCherie2Move(){
		super();
	}
	
	public PGPDelleCherie2Move(int[] cofs) throws Exception{
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
		int choice = 0;
		int score0 = Integer.MIN_VALUE;
		int[][] suc_field = cloneField(field);
		
		for(int i = 0; i < legalMoves.length; i++){
			suc_field = successorField(legalMoves[i], s.getTurnNumber() + 1, piece);
			
			// Get Pile height
			int max_height = findMaxHeight(suc_field);
			int score_next = Integer.MIN_VALUE;
			
			if(max_height <= ROWS){
				// Only proceed to the next move if this move does not cause instant lost
				// ==== IMPT We are removing the complete rows now! =====
				suc_field = collapseField(suc_field);
				suc_field = removeExcess(suc_field);
				
				System.out.println("i = "+i+ "======================================");

				for(int nextPiece = 0; nextPiece < N_PIECES; nextPiece++){
					int score = getBestGreedyScore(suc_field, s.legalMoves(nextPiece),s.getTurnNumber()+2, nextPiece);
					if(score > score_next){
						score_next = score;
					}
				}
				System.out.println("<<<<<<<<< for i = "+i);
			}else{
				System.out.println("Committing suicide now");
			}
				
			

			if(score_next > score0){
				score0 = score_next;
				choice = i;
			}
		}
		
		System.out.println(" =.= Chosen move "+choice+" with score = "+score0);
		return choice;
	}
	
	public int getBestGreedyScore(int[][] nextField, int[][] legalMoves, int turn_number, int nextPiece){
		int score0 = Integer.MIN_VALUE;
		int choice = 0;
		
		for(int i = 0; i < legalMoves.length; i++){
			int[][] suc_field = cloneField(nextField);
			suc_field = successorFieldStrict(suc_field, legalMoves[i], turn_number, nextPiece);
			
			// Get Pile height
			int max_height = findMaxHeight(suc_field);
			if(max_height >= ROWS){
				return Integer.MIN_VALUE;
			}
		
			// Landing Height
			int h_value = findInsertHeight(legalMoves[i], suc_field, nextPiece);
			
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
			
			if(score > score0){
				score0 = score;
				choice = i;
			}
		}
		
		System.out.println("for piece = "+nextPiece+", the best move is with orient "+legalMoves[choice][ORIENT]+" at slot "+legalMoves[choice][SLOT]);
		return score0;
	}
	

	
	public static void main(String args[]){
		
		State s = new State();
		new TFrame(s);
		PGPDelleCherie2Move p = new PGPDelleCherie2Move();
		int times = 2;
//		while(!s.hasLost()) {
		while(times > 0){
			s.makeMove(p.pickMove(s,s.legalMoves()));
			s.draw();
			s.drawNext(0,0);
			times--;
			try {
			Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("You have completed "+s.getRowsCleared()+" rows.");
	}	
	
	
}