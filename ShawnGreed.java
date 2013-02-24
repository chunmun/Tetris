package blueBlox;

class Heuristics {
	private final int WALLS_WEIGHT = 7; //6~8
	private final int FILLED_WEIGHT = 3; //0~10 (not much impact)
	private final int BOTTOM_WEIGHT = 5; //5~8
	private final int LASTPOS_WEIGHT = 25; //30
	
	private double walls;
	private double filled;
	private double bottom;
	private double lastPos;
	
	public Heuristics() {
		walls = 0;
		filled = 0;
		bottom = 0;
		lastPos = 0;
	}
	
	public void calculateHeuristics(int [][]field, int rowPos, int colPos, int bottomBlock) {
		//Assigns score if block comes into contact with another block
		if(colPos - 1 < 0 || field[rowPos][colPos-1] > 0)
			walls++;
		if(colPos + 1 >= State.COLS || field[rowPos][colPos+1] > 0)
			walls++;
		if(rowPos - 1 < 0 || field[rowPos-1][colPos] > 0) {
			walls++;
			if(rowPos -1 >=  0 && field[rowPos-1][colPos] == 0) {
				walls -= 5; //Minus score if leaves a gap
			}
				
		}
		
		//Assigns score base on how low the block is placed
		filled += 1/((rowPos+1.0));
		
		//Assigns score if block is placed at the bottom
		if(rowPos == bottomBlock) {
			bottom++;
		}
		
		//Assigns score if block is placed beside an older block
		if(colPos - 1 >= 0 && field[rowPos][colPos-1] > 0) {
			lastPos += 1.0/(field[rowPos][colPos-1]+1);	
		}
	
		if(colPos + 1 < State.COLS && field[rowPos][colPos+1] > 0) {
			lastPos += 1.0/(field[rowPos][colPos+1]+1);
		}
		if(rowPos - 1 >= 0 && field[rowPos-1][colPos] > 0) {
			lastPos += 1.0/(field[rowPos-1][colPos]+1);
		}
	}
	
	public double getScore() {
		double currentWalls = walls * WALLS_WEIGHT;
		double currentFilled = filled * FILLED_WEIGHT;
		double currentBottom = bottom * BOTTOM_WEIGHT;
		double currentLastPos = lastPos * LASTPOS_WEIGHT; 
		
		double score = currentWalls + currentFilled + currentBottom + currentLastPos;
		return score;
	}
}

public class ShawnGreed implements IPlayer {
	public int pickMove(State s, int[][] legalMoves) {
		int [][]originalField = s.getField();
		
		int nextPiece = s.getNextPiece();
		int chosen = 0;
		
		double score = 0;
		
		int bottomBlock = 100;
		for(int i = 0; i < State.COLS; i++) {
			bottomBlock = Math.min(bottomBlock, s.getTop()[i]);
		}
		
		for(int j = 0; j < legalMoves.length; j++) {
			int orient = legalMoves[j][0];
			int slot = legalMoves[j][1];
			
			Heuristics heuristic = new Heuristics();
			
			int top[] = s.getTop();
			int height = top[slot]-State.getpBottom()[nextPiece][orient][0];
			
			for(int c = 1; c < State.pWidth[nextPiece][orient];c++) {
				height = Math.max(height,top[slot+c]-State.getpBottom()[nextPiece][orient][c]);
			}
			
			if(height + State.getpHeight()[nextPiece][orient] >= 21)
				continue;
			
			for(int i = 0; i < State.pWidth[nextPiece][orient]; i++) {
				for(int h = height+State.getpBottom()[nextPiece][orient][i]; h < height+State.getpTop()[nextPiece][orient][i]; h++) {
					heuristic.calculateHeuristics(originalField, h, i+slot, bottomBlock);
				}
			}
			
			double currentScore = heuristic.getScore();
			
			if(currentScore > score) {
				score = currentScore;
				chosen = j;
			}
		}
		
		return chosen;
	}
}
