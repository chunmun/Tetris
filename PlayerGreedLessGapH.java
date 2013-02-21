package blueBlox;

import java.util.Map;
import java.util.TreeMap;

/**
 * This Player is slightly less greedy than PlayerGreedLeastGap
 * It actually considers the choices that are close to the lowest gap values
 * and choose the one with the least height.
 * 
 * The closeness is determined by TOLERANCE
 * Refer to PlayerGreedLeastGap for more
 * 
 * @author chunmun
 *
 */
public class PlayerGreedLessGapH extends PlayerGreed implements IPlayer {
	protected int TOLERANCE = 1;
	
	public PlayerGreedLessGapH(){
		super();
	}
	
	public PlayerGreedLessGapH(int tol){
		super();
		this.TOLERANCE = tol;
	}
	
	@Override
	protected int makeGreedyChoice(State s, int[][] legalMoves){
		TreeMap<Integer, Choice> g_choices = new TreeMap<Integer, Choice>();
		
		for(int i = 0; i < legalMoves.length; i++){
			int slot = legalMoves[i][1];
			
			int[][] suc_field = successorField(legalMoves[i], s.getTurnNumber() + 1, piece);
			int[] gaps = findGapsPerCol(suc_field);
			int g = sumGaps(gaps);
			int h = getTop(legalMoves[i], suc_field);
//			System.out.println("Move: "+i+" Orient: "+legalMoves[i][0]+" Slot: "+legalMoves[i][1]+" h:"+h+" g:"+g);
			
			if(g_choices.get(g) != null){
				Choice c = g_choices.get(g);
				// Change the choice to the current move if this one has a lower height 
				if(c.h_value > h){
					c.move = i;
					c.h_value = h;
				}
				
			}else{
				g_choices.put(g,new Choice(i,g,h));
			}
		}
		/*
		System.out.print("i: [");
		for(Map.Entry<Integer, Choice> entry : g_choices.entrySet()){
			System.out.print(entry.getValue().move+",");
		}
		System.out.println("]");	
		
		System.out.print("g: [");
		for(Map.Entry<Integer, Choice> entry : g_choices.entrySet()){
			System.out.print(entry.getKey()+",");
		}
		System.out.println("]");
		
		System.out.print("h: [");
		for(Map.Entry<Integer, Choice> entry : g_choices.entrySet()){
			System.out.print(entry.getValue().h_value+",");
		}
		System.out.println("]");	
		*/
		
		// Choose the choices with low g and low h
		int h0 = COLS + 10;
		int found = 0;
		int chosen = 0;
		for(Map.Entry<Integer, Choice> entry : g_choices.entrySet()){
			found++;
			
			if(entry.getValue().h_value < h0){
				chosen = entry.getValue().move;
				h0 = entry.getValue().h_value;
			}
			
//			System.out.println("Chose entry with g_value: "+entry.getKey()+" and h_value: "+entry.getValue().h_value);
			
			if(found == TOLERANCE){
				break;
			}
		}
		
		return chosen;
		
	}
	
	protected int sumGaps(int[] gaps){
		int g = 0;
		for(int j = 0; j < gaps.length; j++){
			g += gaps[j];
		}	
		return g;
	}
	
	protected int getTop(int[] move, int[][] field){
		int[] top = generateFTop(field);
		int orient = move[ORIENT];
		int slot = move[SLOT];
		
		int height = 0; 
		for(int c = 0; c < pWidth[piece][orient]; c++){
			height = Math.max(height, top[slot+c] - pBottom[piece][orient][c]);
		}
		
		return height;
	}
	
	public static void main(String args[]){
		State s = new State();
		new TFrame(s);
		PlayerGreedLessGapH p = new PlayerGreedLessGapH();
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

