package blueBlox;

/**
 * This version of the Greedy Player tries to make moves that results in 
 * the lowest possible height increment using the current piece for 
 * the next move with forward-checking depth of 1
 * 
 * @author chunmun
 *
 */
public class PlayerGreedLeastH extends PlayerGreed implements IPlayer {
	@Override
	protected int makeGreedyChoice(State s, int[][] legalMoves){
		int h0 = ROWS + 10;
		int choice = 0;
		
		for(int i = 0; i < legalMoves.length; i++){
			int[][] suc_field = successorField(legalMoves[i], s.getTurnNumber() + 1, piece);
			int h = findMaxHeight(suc_field);
			
			if(h < h0){
				choice = i;
				h0 = h;
			}
		}		
		
		return choice;
}