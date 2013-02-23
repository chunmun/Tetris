package blueBlox;

import java.util.Vector;

public class StateGenerator {
	public static final int N_PIECES = 7;
	
	public static enum SG_TYPE { RANDOM, FIXED, CYCLIC, FUNC }; 
	public SG_TYPE type = SG_TYPE.RANDOM; 
	
	private int fixed_piece = 0;
	private Vector<Integer> piece_seq = new Vector<Integer>();
	
	// For the cyclic type
	private int[] cycle_seq;
	
	public StateGenerator(SG_TYPE type){
		this.type = type;
	}
	
	public void setFixedPiece(int piece){
		fixed_piece = piece;
	}
	
	public void setCycle(int[] seq){
		this.cycle_seq = seq;
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
		case CYCLIC:
			int ind = turn % cycle_seq.length;
			return Math.abs(cycle_seq[ind] % N_PIECES);
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
