package blueBlox;

import java.util.Vector;

public class StateGenerator {
	public static final int N_PIECES = 7;
	
	public static enum SG_TYPE { RANDOM, FIXED, FUNC };
	
	public SG_TYPE type = SG_TYPE.RANDOM; 
	
	private int fixed_piece = 0;
	private Vector<Integer> piece_seq = new Vector<Integer>();
	
	public StateGenerator(SG_TYPE type){
		this.type = type;
	}
	
	public void setFixedPiece(int piece){
		fixed_piece = piece;
	}
	
	public int nextPiece(int turn){
		int next = getNextPiece(turn);
		piece_seq.add(next);
		return next;
	}
	
	private int getNextPiece(int turn){
		switch(type){
		case RANDOM:
			return (int)(Math.random()*N_PIECES);
		case FIXED:
			return fixed_piece;
		case FUNC:
			return funcNextPiece(turn);
		}
		return 0;
	}
	
	// Meant for override 
	private int funcNextPiece(int turn){
		return turn % (N_PIECES+1);
	}
	
	public String getSeq(){
		return piece_seq.toString();
	}
	
	public void reset(){
		
	}
	
}
