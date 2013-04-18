package blueBlox;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

public class PlayerGMixedRankGHF extends PlayerGreed implements IPlayer{
	@Override
	public int makeGreedyChoice(State s, int[][] legalMoves){
		Vector<Choice> choices = new Vector<Choice>();
		
		for(int i = 0; i < legalMoves.length; i++){
			int[][] suc_field = successorField(legalMoves[i], s.getTurnNumber() + 1, piece);
			
			// Compute Gap value
			int[] gaps = findDeadGapsPerCol(suc_field);
			int g_value = sumGaps(gaps);
			
			// Compute Flatness value
			int f_value =  getTopDiff(generateFTop(suc_field));
			
			// Compute Height value
			int h_value = getTop(legalMoves[i], suc_field);
			
			Choice c = new Choice(i);
			c.g_value = g_value;
			c.h_value = h_value;
			c.f_value = f_value;
			
			choices.add(c);
		}
		
//		for(int i = 0; i < choices.size(); i++){
//			System.out.println(choices.get(i));
//		}	
		
		Collections.sort(choices,new compareG());
		int last_value = 999999;
		int last_rank = 0;
		for(int i = 0; i < choices.size(); i++){
			int g = choices.get(i).g_value;
			if(last_value == g){
				choices.get(i).g_value = last_rank;
			}else{
				choices.get(i).g_value = i;
				last_rank = i;
			}
			last_value = g;
		}	
		
		Collections.sort(choices,new compareF());
		last_value = 999999;
		last_rank = 0;
		for(int i = 0; i < choices.size(); i++){
			int f = choices.get(i).f_value;
			if(last_value == f){
				choices.get(i).f_value = last_rank;
			}else{
				choices.get(i).f_value = i;
				last_rank = i;
			}
			last_value = f;
		}	
		
		Collections.sort(choices,new compareH());
		last_value = 999999;
		last_rank = 0;
		for(int i = 0; i < choices.size(); i++){
			int h = choices.get(i).h_value;
			if(last_value == h){
				choices.get(i).h_value = last_rank;
			}else{
				choices.get(i).h_value = i;
				last_rank = i;
			}
			last_value = h;
		}			
		
//		for(int i = 0; i < choices.size(); i++){
//			System.out.println(choices.get(i));
//		}	
		
		int choice = 0;
		int score0 = 9999999;
		for(int i = 0; i < choices.size(); i++){
			Choice c = choices.get(i);
			int score = c.f_value * 1 + c.g_value * 3 + c.h_value * 5; 
			
			if(score < score0){
				score0 = score;
				choice = c.move;
			}
		}
//		System.out.println("Chosen move "+choice);
		
		return choice;
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
		PlayerGMixedRankGHF p = new PlayerGMixedRankGHF();
		while(!s.hasLost()) {
//			s.setNextPiece(0);
			s.makeMove(p.pickMove(s,s.legalMoves()));
			s.draw();
			s.drawNext(0,0);
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("You have completed "+s.getRowsCleared()+" rows.");
	}	
}

class compareF implements Comparator<Choice>{
	@Override
	public int compare(Choice c1, Choice c2) {
		if(c1.f_value < c2.f_value){ return -1; }
		if(c1.f_value > c2.f_value){ return 1; }
		else{ return 0; }
	}
}
class compareG implements Comparator<Choice>{
	@Override
	public int compare(Choice c1, Choice c2) {
		if(c1.g_value < c2.g_value){ return -1; }
		if(c1.g_value > c2.g_value){ return 1; }
		else{ return 0; }
	}
}
class compareH implements Comparator<Choice>{
	@Override
	public int compare(Choice c1, Choice c2) {
		if(c1.h_value < c2.h_value){ return -1; }
		if(c1.h_value > c2.h_value){ return 1; }
		else{ return 0; }
	}
}