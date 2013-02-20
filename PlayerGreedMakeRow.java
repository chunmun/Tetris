package blueBlox;

/**
 * This Player greedily chooses the move that completes the most rows
 * The height of each move is used as a tie-breaker
 * 
 * @author chunmun
 *
 */
public class PlayerGreedMakeRow extends PlayerGreed implements IPlayer {
	@Override
	protected int makeGreedyChoice(State s, int[][] legalMoves){
		int h0 = ROWS + 10;
		int r0 = 0;
		int h_choice = 0;
		int r_choice = 0;
		
		for(int i = 0; i < legalMoves.length; i++){
			int[][] suc_field = successorField(legalMoves[i], s.getTurnNumber() + 1, piece);
			int h = findMaxHeight(suc_field);
			int r = findCompleteRows(suc_field).length;
			
			if(h < h0){
				h_choice = i;
				h0 = h;
			}
			
			if(r > r0){
				r_choice = i;
				r0 = r;
			}
		}		
		
		if(r0 > 0){
			return r_choice;
		}else{
			return h_choice;
		}
	}
	
	public static void main(String[] args) {
		State s = new State();
		new TFrame(s);
		PlayerGreedMakeRow p = new PlayerGreedMakeRow();
		while(!s.hasLost()) {
			s.setNextPiece(1);
			s.makeMove(p.pickMove(s,s.legalMoves()));
			s.draw();
			s.setNextPiece(1);
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
