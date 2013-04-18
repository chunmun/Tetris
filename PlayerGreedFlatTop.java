package blueBlox;

/**
 * This player picks move that reduces the irregularities in the top surface
 * For example top[i+1] - top[i] should be be minimized for all i 
 * 
 * @author chunmun
 *
 */
public class PlayerGreedFlatTop extends PlayerGreed implements IPlayer{
	@Override
	public int makeGreedyChoice(State s, int[][] legalMoves){
		int choice = 0;
		int d0 = ROWS * COLS;
		
		for(int i = 0; i < legalMoves.length; i++){
			int[][] suc_field = successorField(legalMoves[i], s.getTurnNumber() + 1, piece);
			int[] suc_top = generateFTop(suc_field);
			int d = getTopDiff(suc_top);
			
			if(d < d0){
				d0 = d;
				choice = i;
			}
		}
		
		return choice;
	}
	

	
	public static void main(String args[]){
		State s = new State();
		new TFrame(s);
		PlayerGreedFlatTop p = new PlayerGreedFlatTop();
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
