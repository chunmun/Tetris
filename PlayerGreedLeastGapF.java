package blueBlox;

/**
 * This player attempts to choose the move which produces the least gap followed 
 * by the move with the flater surface
 * 
 * @author chunmun
 *
 */
public class PlayerGreedLeastGapF extends PlayerGreed implements IPlayer{
	@Override
	protected int makeGreedyChoice(State s, int[][] legalMoves){
		int choice = 0;
		int t0 = ROWS * COLS;
		int g0 = 9999; // Arbitrary large number
		
		for(int i = 0; i < legalMoves.length; i++){
			int slot = legalMoves[i][1];
			
			int[][] suc_field = successorField(legalMoves[i], s.getTurnNumber() + 1, piece);
			int[] gaps = findDeadGapsPerCol(suc_field);
			int g = sumGaps(gaps);
			int t = getTopDiffSq(generateFTop(suc_field));
			
			if(g < g0){
				choice = i;
				g0 = g;
				
				// We reset the h_choice if we find a better g
				t0 = t;
			}
			
			if(t < t0 && g0 == g){ // Only choose the lowest height given the current lowest g
				choice = i;
				t0 = t;
				
			}
		}
		
		return choice;
	}
	
	protected int sumGaps(int[] gaps){
		int g = 0;
		for(int j = 0; j < gaps.length; j++){
			g += gaps[j];
		}	
		return g;
	}
	
	
	public static void main(String args[]){
		State s = new State();
		new TFrame(s);
		PlayerGreedLeastGapF p = new PlayerGreedLeastGapF();
		while(!s.hasLost()) {
			s.makeMove(p.pickMove(s,s.legalMoves()));
			s.draw();
			s.drawNext(0,0);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("You have completed "+s.getRowsCleared()+" rows.");
	}
}
