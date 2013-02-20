package blueBlox;

/**
 * This version of the Greedy Player tries to make moves that results in 
 * the lowest possible height increment using the current piece for 
 * the next move with forward-checking depth of 1
 * 
 * @author chunmun
 *
 */
public class PlayerGreedLowHeight extends PlayerGreed implements IPlayer {
	@Override
	protected int makeGreedyChoice(State s, int[][] legalMoves){
		int n0 = ROWS + 10;
		int choice = 0;
		
		for(int i=0;i<legalMoves.length;i++){
			int[][] suc_field = successorField(legalMoves[i], s.getTurnNumber() + 1, piece);
			int n = findMaxHeight(suc_field);
			
			if(n < n0){
				choice = i;
				n0 = n;
			}
		}		
		
		for(int i=0;i<legalMoves.length;i++){
			int[][] suc_field = successorField(legalMoves[i], s.getTurnNumber() + 1, piece);
			int n = findMaxHeight(suc_field);
			
			if(n < n0){
				choice = i;
				n0 = n;
			}
		}
		
		return choice;	}
	
	protected int findMaxHeight(int[][] field){
		int height = 0;
		int[] top = generateFTop(field);
		for(int i = 0; i < top.length; i++){
			height = Math.max(height, top[i]);
		}
		return height;
	}
	

}
