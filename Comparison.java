package blueBlox;

import java.util.Arrays;
import java.util.Random;

public class Comparison {
	protected static boolean SHOW = false;
	public static void main(String args[]) throws Exception{
		PDelleFast pf = new PDelleFast(new double[] {-1,1,-1,-1,-6,-2,0});
		PlayerGPDelleCherie pd = new PlayerGPDelleCherie(new int[] {-1,1,-1,-1,-6,-2});
		
		State state_pf = new State();
		State state_pd = new State();
		
		TFrame t1 = new TFrame(state_pf);
		TFrame t2 = new TFrame(state_pd);
		
//		for(int i = 0; i < 4; i++){
//			for(int j = 0; j < 10 - 1; j++){
//				state_pf.setNextPiece(1);
//				state_pd.setNextPiece(1);
//				state_pf.makeMove(j);
//				state_pd.makeMove(j);
//			}
//		}
//		
//		for(int i = 0; i < 2; i++){
//			for(int j = 0; j < 2; j++){
//				state_pf.setNextPiece(1);
//				state_pd.setNextPiece(1);
//				state_pf.makeMove(j + i * 4 + 10);
//				state_pd.makeMove(j + i * 4 + 10);
//			}
//		}
		
		Random r = new Random(1);
		while(!state_pf.hasLost() && !state_pd.hasLost()){
			int piece = r.nextInt(7);
			state_pf.setNextPiece(piece);
			state_pd.setNextPiece(piece);
			
			int choice_pf = pf.pickMove(state_pf, state_pf.legalMoves());
			int choice_pd = pd.pickMove(state_pd, state_pd.legalMoves());
			
			if(SHOW){
			state_pf.draw();
			state_pd.draw();
			}
			
			int[] move_pf = state_pf.legalMoves()[choice_pf];
			int[] move_pd = state_pd.legalMoves()[choice_pd];

			if(SHOW){
			state_pf.drawNext(move_pf[0], move_pf[1]);
			state_pd.drawNext(move_pd[0], move_pd[1]);
			}
		
			
//			if(differentFields(state_pf.getField(), state_pd.getField())){
//				printField(state_pf.getField());
//				printField(state_pd.getField());
//				System.out.println("There's something wrong with the fields");
//				return;
//			}
			
			if(choice_pf != choice_pd || (state_pf.hasLost() || state_pd.hasLost())){
				System.out.println("Player Delle");
				pd.printLastChoice();
				
				System.out.println("Fast Delle");
				pf.printLastChoice();
				pf.printLastG();
				
				state_pf.draw();
				state_pd.draw();
				state_pf.drawNext(move_pf[0], move_pf[1]);
				state_pd.drawNext(move_pd[0], move_pd[1]);
				
				System.out.println("Piece: "+piece);
				System.out.println("Fast choice: "+choice_pf);
				System.out.println("Delle choice: "+choice_pd);
				break;
			}	
			
			state_pf.makeMove(choice_pf);
			state_pd.makeMove(choice_pd);
		}
	}
	
	protected static void printField(int[][] field){
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
	
	protected static boolean differentFields(int[][] f1, int[][] f2){
		for(int i = 0; i < f1.length; i++){
			for(int j = 0; j < f1[0].length; j++){
				if(f1[i][j] != f2[i][j]){
					return true;
				}
			}
		}
		return false;
	}
}
