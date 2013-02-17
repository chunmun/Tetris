package blueBlox;

public class PlayerRandom implements IPlayer {

	@Override
	public int pickMove(State s, int[][] legalMoves) {
		int orient = legalMoves.length;
		return (int)(orient * Math.random());
	}

}
