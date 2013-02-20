package blueBlox;

/**
 * Superclass of Greedy Players
 * Meant to be extended and makeGreedyChoice should be overwritten with a Greedy Algo
 * 
 * @author chunmun
 *
 */
public class PlayerGreed implements IPlayer {
	protected int[] pOrient;		// #orients per piece
	protected int[][] pWidth;		// [piece][orient] width
	protected int[][] pHeight;	// [piece][orient] max height
	protected int[][][] pBottom;	// [piece][orient][slots]
	protected int[][][] pTop;	// [piece][orient][slots]
	
	protected int piece;
	
	protected int[][] field;
	protected int[] fTop;
	protected int ROWS;
	protected int COLS;
	
	protected int ORIENT = 0;
	protected int SLOT = 1;	// Index of legalMoves[piece]
	
	protected boolean init = false;
	
	@Override
	public int pickMove(State s, int[][] legalMoves) {
		// Load field if not loaded yet
		field = s.getField();
		fTop = s.getTop();
		piece = s.getNextPiece();
		loadAttributes(s);
		
		int choice = makeGreedyChoice(s,legalMoves);

		return choice;
	}
	
	protected int makeGreedyChoice(State s, int[][] legalMoves){

		return 0; // Placeholder code
	}
	
	protected void loadAttributes(State s){
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
	
	protected int[][] successorField(int[] move, int turn, int piece){
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
		
	protected void printField(int[][] field){
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
	
	protected int[] generateFTop(int[][] field){
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
}
