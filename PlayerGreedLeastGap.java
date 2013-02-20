package blueBlox;

/**
 * This player chooses a move the minimize the number of gaps in the successor field
 * A gap is a field[i][j] where 
 * 1. field[i][j] = 0
 * 2. field[k][j] > 0 for k > i, ie there is a brick above this hole 
 * 
 * The height of the move is used as tie-breaker
 * IMPT! This height is the height of the move, not the height of the overall field
 * using the height of the overall field causes problems 
 * 
 * @author chunmun
 *
 */
public class PlayerGreedLeastGap extends PlayerGreed implements IPlayer{
	@Override
	protected int makeGreedyChoice(State s, int[][] legalMoves){
		int g_choice = 0;
		int h_choice = 0;
		int g0 = 9999; // Arbitrary large number
		int h0 = ROWS + 10;
		
		for(int i = 0; i < legalMoves.length; i++){
			int slot = legalMoves[i][1];
			
			int[][] suc_field = successorField(legalMoves[i], s.getTurnNumber() + 1, piece);
			int[] gaps = findGapsPerCol(suc_field);
			int g = sumGaps(gaps);
			int h = fTop[slot];
			
			if(g < g0){
				g_choice = i;
				g0 = g;
				
				// We reset the h_choice if we find a better g
				h0 = h;
				h_choice = i;
			}
			
			if(h < h0 && g0 == g){ // Only choose the lowest height given the current lowest g
				h_choice = i;
				h0 = h;
				
			}
		}
		
		return h_choice;
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
		PlayerGreedLeastGap p = new PlayerGreedLeastGap();
		while(!s.hasLost()) {
			s.makeMove(p.pickMove(s,s.legalMoves()));
			s.draw();
			s.drawNext(0,0);
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("You have completed "+s.getRowsCleared()+" rows.");
	}
	
}
