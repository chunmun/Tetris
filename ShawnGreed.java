package blueBlox;

class Heuristics {
	private final int WALLS_WEIGHT = 7;
	private final int FILLED_WEIGHT = 3;
	private final int BOTTOM_WEIGHT = 5;
	
	private double walls;
	private double filled;
	private double bottom;
	
	public Heuristics() {
		walls = 0;
		filled = 0;
		bottom = 0;
	}
	
	public void calculateHeuristics(int [][]field, int rowPos, int colPos, int bottomBlock) {
		//Check contact with walls
		if(colPos - 1 < 0 || field[rowPos][colPos-1] > 0)
			walls++;
		if(colPos + 1 >= State.COLS || field[rowPos][colPos+1] > 0)
			walls++;
		if(rowPos - 1 < 0 || field[rowPos-1][colPos] > 0)
			walls++;
		
		//Check position block fills
		filled += 1/((rowPos+1.0)/10);
		
		//Checks if block fills the bottom
		if(rowPos == bottomBlock) {
			bottom++;
		}
	}
	
	public double getScore() {
		double currentWalls = walls * WALLS_WEIGHT;
		double currentFilled = filled * FILLED_WEIGHT;
		double currentBottom = bottom *BOTTOM_WEIGHT;
		
		double score = currentWalls + currentFilled + currentBottom;
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
		
		//System.out.println("Lines broken: " + s.getRowsCleared());
		return chosen;
	}
}
