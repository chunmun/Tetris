package blueBlox;

import java.util.Vector;

public class PlayerGreedLowHeight implements IPlayer {
	private int[] pOrient;		// #orients per piece
	private int[][] pWidth;		// [piece][orient] width
	private int[][] pHeight;	// [piece][orient] max height
	private int[][][] pBottom;	// [piece][orient][slots]
	private int[][][] pTop;	// [piece][orient][slots]
	
	private int[][] field;
	private int[] fTop;
	private int ROWS;
	private int COLS;
	
	private int ORIENT = 0;
	private int SLOT = 1;	// Index of legalMoves[piece]
	
	private boolean init = false;
	
	@Override
	public int pickMove(State s, int[][] legalMoves) {
		int piece = s.getNextPiece();
		
		// Load field if not loaded yet
		field = s.getField();
		fTop = s.getTop();
		loadField(s);
		
		int n0 = ROWS + 10; // Arbitrary value int choice = 0;
		int choice = 0;
		for(int i=0;i<legalMoves.length;i++){
			int[][] suc_field = successorField(legalMoves[i], s.getTurnNumber() + 1, piece);
			int n = findMaxHeight(suc_field);
			
			if(n < n0){
				choice = i;
				n0 = n;
			}
		}
		return choice;
	}
	
	private void loadField(State s){
		if(!init){
			pOrient = s.getpOrients();
			pWidth = s.getpWidth();
			pHeight = s.getpHeight();
			pBottom = s.getpBottom();
			pTop = s.getpTop();
			
			ROWS = field.length;
			COLS = field[0].length;
			
			init = true;
		}else{
			return;
		}
	}
	
	private int[][] successorField(int[] move, int turn, int piece){
		int[][] suc_field = new int[ROWS+4][COLS]; // Increased rows to accommodate for calculations at the border
		
		// Deep clone
		for(int i = 0; i < field.length; i++){
			for(int j = 0; j < field[0].length; j++){
				suc_field[i][j] = field[i][j];
			}
		}
		
		int orient = move[ORIENT];
		int slot = move[SLOT];
		
		int height = 0; 
		for(int c = 0; c < pWidth[piece][orient]; c++){
			height = Math.max(height, fTop[slot+c] - pBottom[piece][orient][c]);
		}
		
		for(int i = 0; i < pWidth[piece][orient]; i++){
			for(int j = height + pBottom[piece][orient][i]; j < height+pTop[piece][orient][i]; j++){
				suc_field[j][slot+i] = turn;
			}
		}
		
		return suc_field;
	}
	
	private int findMaxHeight(int[][] field){
		int height = 0;
		int[] top = generateFTop(field);
		for(int i = 0; i < top.length; i++){
			height = Math.max(height, top[i]);
		}
		return height;
	}
	
	private int[] generateFTop(int[][] field){
		int new_top[] = new int[COLS];
		for(int i = 0; i < COLS; i++){
			for(int j = 0; j < ROWS; j++){
				if(field[j][i] != 0){
					new_top[i] = j;
					continue;
				}
			}
		}
		return new_top;
	}
	
	public static void main(String[] args) {
		State s = new State();
		new TFrame(s);
		PlayerGreedLowHeight p = new PlayerGreedLowHeight();
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
		
		
	public void printField(int[][] field){
		System.out.println("{");
		for(int i = 0; i < field.length; i++){
			System.out.print("[");
			for(int j = 0; j < field[0].length; j++){
				System.out.print(field[i][j]+",");
			}
			System.out.println("]");
		}
		System.out.println("}");
	}
	

}
