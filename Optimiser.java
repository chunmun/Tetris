package blueBlox;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.Vector;

import blueBlox.StateGenerator.SG_TYPE;

public class Optimiser {
	//		int cof_h = -1, cof_r = 1, cof_row = -1, cof_col = -1, cof_g = -5, cof_w = -1; // This is the hand-cranked values resulting in an avg #lines 6800

	static int max_rows_cleared = 500000;
	
	public static void main(String args[]) throws FileNotFoundException{
		int sign_h = -1, sign_r = 1, sign_row = -1, sign_col = -1, sign_g = -1, sign_w = -1;
		int tries = 1;
		int num_variables = 6;

		// 1. Fix the domain that we're going to use for all parameters
		int ALL_MAX = 10;
		int ALL_MIN = 0;
		//		int max_h = ALL_MAX, max_r = ALL_MAX, max_row = ALL_MAX, max_col = ALL_MAX, max_g = ALL_MAX, max_w = ALL_MAX;
		//		int min_h = ALL_MIN, min_r = ALL_MIN, min_row = ALL_MIN, min_col = ALL_MIN, min_g = ALL_MIN, min_w = ALL_MIN;

		int[] max_vector = {ALL_MAX, ALL_MAX, ALL_MAX, ALL_MAX, ALL_MAX, ALL_MAX};
		int[] min_vector = {ALL_MIN, ALL_MIN, ALL_MIN, ALL_MIN, ALL_MIN, ALL_MIN}; 
		int[] sign_vec = {-1, 1, -1, -1, -1, -1};
		Vector<Record> scores = new Vector<Record>();

//		int[] cur_vec = new int[num_variables];
		int[] cur_vec = {0, 4, -3, -5, -10, -3};
		int[] signed_cur_vec = new int[num_variables];
//		copyVector(min_vector, cur_vec);

		copyVector(cur_vec, signed_cur_vec);
		applySigns(signed_cur_vec, sign_vec);
		int num = 1;
		
		PrintStream fileStdout = new PrintStream(new FileOutputStream("C:\\Users\\Chunmun\\OptimiserLog.txt"));

		while(tries > 0){
			// Set up the required Sequence and State Generator
			int[] cycle_seq = generateRandomSeq(1000);
			StateGenerator seq = new StateGenerator(SG_TYPE.CYCLIC);
			seq.setCycle(cycle_seq);
			
			while(true){
				// Try them out on the each vector one at a time
				try{
					System.out.print("Trial : "+ Arrays.toString(signed_cur_vec));
					// DO NOT use a random StateGenerator, use a Cyclic one
					int score = runTest(new PlayerGPDelleCherie(signed_cur_vec), seq);
					scores.add(new Record(signed_cur_vec,score));
					System.out.println(" => "+score);
					
					cur_vec = next_vector(cur_vec, min_vector, max_vector);
					copyVector(cur_vec, signed_cur_vec);
					applySigns(signed_cur_vec, sign_vec);
					
					fileStdout.println("Trials : "+ Arrays.toString(signed_cur_vec)+" => "+"Score : " + score);
					
					num++;
				}catch(Exception e){
					// Reached the max_vector
					System.out.println("Finished");
					e.printStackTrace();
					break;
				}
			}
			
			tries--;
		}
		
		fileStdout.close();
		
		Collections.sort(scores);
		for(Record r : scores){
			System.out.println(r);
		}
	}

	public static int runTest(PlayerGPDelleCherie player, StateGenerator sg){
		State s = new State();
		long last_time = System.currentTimeMillis();
		
		while(!s.hasLost() && s.getRowsCleared() <= max_rows_cleared){
			s.setNextPiece(sg.nextPiece(s.getTurnNumber()));
			s.makeMove(player.pickMove(s,s.legalMoves()));
			
			if(System.currentTimeMillis() - last_time > 10000){
				System.out.print("[" + s.getTurnNumber()+ ","+s.getRowsCleared() + ", ");
				last_time = System.currentTimeMillis();
			}
		}
		
		return s.getRowsCleared();
	}
	
	public static int[] generateRandomSeq(int len){
		Random r = new Random();
		int[] res = new int[len];
		
		for(int i = 0; i < len; i++){
			res[i] = r.nextInt();
		}
		
		return res;
	}

	public static void applySigns(int[] cur_vec, int[] sign_vec){
		for(int i = 0; i < cur_vec.length; i++){
			cur_vec[i] *= sign_vec[i];
		}
	}

	public static int[] next_vector(int[] cur_vec, int[] min_vec, int[] max_vec) throws Exception{
		int[] res_vec = new int[cur_vec.length];
		int carry = 1;
		for(int i = cur_vec.length - 1; i >= 0; i--){
			res_vec[i] = cur_vec[i] + carry;
			if(res_vec[i] > max_vec[i]){ 
				carry = 1;
				res_vec[i] = min_vec[i];
			}else{
				carry = 0;
			}
		}
		if(carry == 1){
			throw new Exception("Max vec reached");
		}
		return res_vec;
	}

	public static void copyVector(int[] copyFrom, int[] copyTo){
		for(int i = 0; i < copyFrom.length; i++){
			copyTo[i] = copyFrom[i];
		}
	}
}

class Record implements Comparable<Record>{
	public int[] vector = new int[0];
	public int score = Integer.MIN_VALUE;
	public Record(int[] vector, int score){
		this.vector = new int[vector.length];
		for(int i = 0; i < vector.length; i++){
			this.vector[i] = vector[i];
		}
		this.score = score;
	}
	
	public String toString(){
		String res = "Score : " + score + " Vector ";
		
		for(int i = 0; i < vector.length; i++){
			res += vector[i] + " , ";
		}
		
		return res;
	}

	@Override
	public int compareTo(Record other) {
		if(score < other.score){
			return -1;
		}
		if(score == other.score){
			return 0;
		}
		return 1;
	}
}
