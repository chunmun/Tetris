package blueBlox;

/**
 * This player chooses a move the minimize the number of gaps in the successor field
 * A gap is a field[i][j] where 
 * 1. field[i][j] = 0
 * 2. field[k][j] > 0 for k > i, ie there is a brick above this hole 
 * 
 * The number of rows completed is used as tie-breaker
 * IMPT! This height is the height of the inserted move, not the height of the overall field
 * using the height of the overall field causes problems 
 * 
 * @author chunmun
 *
 */
public class PlayerGLeastGMake extends PlayerGreed implements IPlayer{
	@Override
	protected int makeGreedyChoice(State s, int[][] legalMoves){
		int choice = 0;
		int g0 = 9999; // Arbitrary large number
		int h0 = 0;
		
		for(int i = 0; i < legalMoves.length; i++){
			int slot = legalMoves[i][1];
			
			int[][] suc_field = successorField(legalMoves[i], s.getTurnNumber() + 1, piece);
			int[] gaps = findDeadGapsPerCol(suc_field);
			int g = sumGaps(gaps);
			int h = findCompleteRows(suc_field).length;
			
			if(g < g0){
				choice = i;
				g0 = g;
				
				// We reset the h_choice if we find a better g
				h0 = h;
			}
			
			if(h > h0 && g0 == g){ // Only choose the lowest height given the current lowest g
				choice = i;
				h0 = h;
				
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
		PlayerGLeastGMake p = new PlayerGLeastGMake();
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
