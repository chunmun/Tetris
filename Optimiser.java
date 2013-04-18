package blueBlox;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.Vector;

import blueBlox.StateGenerator.SG_TYPE;

public class Optimiser extends Thread{
	//		int cof_h = -1, cof_r = 1, cof_row = -1, cof_col = -1, cof_g = -5, cof_w = -1; // This is the hand-cranked values resulting in an avg #lines 6800

	static int max_rows_cleared = 10000;
	static int num_trials_per_param = 100;
	
	// 1. Fix the domain that we're going to use for all parameters
	static int ALL_MAX = 10;
	static int ALL_MIN = 0;
	//		int max_h = ALL_MAX, max_r = ALL_MAX, max_row = ALL_MAX, max_col = ALL_MAX, max_g = ALL_MAX, max_w = ALL_MAX;
	//		int min_h = ALL_MIN, min_r = ALL_MIN, min_row = ALL_MIN, min_col = ALL_MIN, min_g = ALL_MIN, min_w = ALL_MIN;

	static int[] maximum_vec = {ALL_MAX, ALL_MAX, ALL_MAX, ALL_MAX, ALL_MAX, ALL_MAX};
	static int[] minimum_vec = {ALL_MIN, ALL_MIN, ALL_MIN, ALL_MIN, ALL_MIN, ALL_MIN};


	int[] min_vector = new int[6];
	int[] max_vector = new int[6];

	int lognum = 0;

	public Optimiser(int[] min_vector, int[] max_vector, int lognum){
		this.min_vector = min_vector;
		this.max_vector = max_vector;
		this.lognum = lognum;
		System.out.println("Optimiser "+lognum+" started from "+Arrays.toString(min_vector)+" - "+Arrays.toString(max_vector));
	}

	public static void main(String args[]) throws FileNotFoundException{
		int[] cur_vec = {0,0,0,0,0,0};
		int[][] start_vec = new int[5][6];
		int[][] end_vec = new int[5][6];
		

		start_vec[0] = new int[]{0,0,6,3,7,7};
		start_vec[1] = new int[]{2,0,0,5,6,3};
		start_vec[2] = new int[]{4,0,0,2,8,3};
		start_vec[3] = new int[]{6,0,0,3,7,10};
		start_vec[4] = new int[]{8,0,0,6,10,7};
		
		end_vec[0] = new int[]{2,0,0,0,0,0};
		end_vec[1] = new int[]{4,0,0,0,0,0};
		end_vec[2] = new int[]{6,0,0,0,0,0};
		end_vec[3] = new int[]{8,0,0,0,0,0};
		end_vec[4] = new int[]{0,0,0,0,0,0};
		

		for(int total = 0; total < 5; total++){

//			int[] start_vec = new int[6];
//			for(int j = 0; j < 6; j++){
//				start_vec[j] = cur_vec[j];
//			}
//
//			for(int num = 0; num < 200000; num++){
//				int carry = 1;
//				for(int i = cur_vec.length - 1; i >= 0; i--){
//					cur_vec[i] = cur_vec[i] + carry;
//					if(cur_vec[i] >= maximum_vec[i]){ 
//						carry = 1;
//						cur_vec[i] = minimum_vec[i];
//					}else{
//						carry = 0;
//					}
//				}
//			}
//
//			int[] end_vec = new int[6];
//			for(int j = 0; j < 6; j++){
//				end_vec[j] = cur_vec[j];
//			}

			Thread th = new Optimiser(start_vec[total], end_vec[total], total);
			th.start();
		}
	}

	public void run(){
		int sign_h = -1, sign_r = 1, sign_row = -1, sign_col = -1, sign_g = -1, sign_w = -1;
		int tries = 1;
		int num_variables = 6;


		int[] sign_vec = {-1, 1, -1, -1, -1, -1};
		Vector<Record> scores = new Vector<Record>();

		int[] cur_vec = new int[num_variables];
		int[] signed_cur_vec = new int[num_variables];
		copyVector(min_vector, cur_vec);

		copyVector(cur_vec, signed_cur_vec);
		applySigns(signed_cur_vec, sign_vec);
		int num = 1;

		PrintStream fileStdout = null;

		try {
			fileStdout = new PrintStream(new FileOutputStream("C:\\Users\\Chunmun\\OptimiserLog"+lognum+".txt"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		fileStdout.println("@RELATION Tetris Parameters");
		fileStdout.println();
		fileStdout.println("@ATTRIBUTE h_value NUMERIC");
		fileStdout.println("@ATTRIBUTE r_value NUMERIC");
		fileStdout.println("@ATTRIBUTE row_value NUMERIC");
		fileStdout.println("@ATTRIBUTE col_value NUMERIC");
		fileStdout.println("@ATTRIBUTE g_value NUMERIC");
		fileStdout.println("@ATTRIBUTE w_value NUMERIC");
		fileStdout.println("@ATTRIBUTE score NUMERIC");
		fileStdout.println();
		fileStdout.println("@DATA");


		/*
		 *    @RELATION iris

   @ATTRIBUTE sepallength  NUMERIC
   @ATTRIBUTE sepalwidth   NUMERIC
   @ATTRIBUTE petallength  NUMERIC
   @ATTRIBUTE petalwidth   NUMERIC
   @ATTRIBUTE class        {Iris-setosa,Iris-versicolor,Iris-virginica}
		 */

		while(tries > 0){
			// Set up the required Sequence and State Generator
			//			int[] cycle_seq = generateRandomSeq(1000);
			StateGenerator seq = new StateGenerator(SG_TYPE.RANDOM);
			//			seq.setCycle(cycle_seq);

			while(true){
				// Try them out on the each vector one at a time
				try{
					System.out.print("Trial "+lognum+" : "+ Arrays.toString(signed_cur_vec));
					// DO NOT use a random StateGenerator, use a Cyclic one
					int score = runTest(new PlayerGPDelleCherie(signed_cur_vec), seq);
					scores.add(new Record(signed_cur_vec,score));
					System.out.println(" => "+score);

					cur_vec = next_vector(cur_vec, min_vector, max_vector);
					copyVector(cur_vec, signed_cur_vec);
					applySigns(signed_cur_vec, sign_vec);

					String st = "";
					for(int i = 0; i < signed_cur_vec.length; i++){
						st += signed_cur_vec[i]+",";
					}

					fileStdout.println(st+score);

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
		System.out.println("Optimiser "+lognum+" stopped");

		Collections.sort(scores);
		for(Record r : scores){
			System.out.println(r);
		}
	}

	public int runTest(PlayerGPDelleCherie player, StateGenerator sg){
		int total_rows = 0;
		for(int i = 0; i < num_trials_per_param; i++){
			blueBlox.State s = new blueBlox.State();
			long last_time = System.currentTimeMillis();

			while(!s.hasLost() && s.getRowsCleared() <= max_rows_cleared){
				s.setNextPiece(sg.nextPiece(s.getTurnNumber()));
				s.makeMove(player.pickMove(s,s.legalMoves()));

				if(System.currentTimeMillis() - last_time > 10000){
					System.out.print("[" + s.getTurnNumber()+ ","+s.getRowsCleared() + ", ");
					last_time = System.currentTimeMillis();
				}
			}
			total_rows += s.getRowsCleared();
		}

		return total_rows/num_trials_per_param;
	}

	public int[] generateRandomSeq(int len){
		Random r = new Random();
		int[] res = new int[len];

		for(int i = 0; i < len; i++){
			res[i] = r.nextInt();
		}

		return res;
	}

	public void applySigns(int[] cur_vec, int[] sign_vec){
		for(int i = 0; i < cur_vec.length; i++){
			cur_vec[i] *= sign_vec[i];
		}
	}




	public int[] next_vector(int[] cur_vec, int[] min_vec, int[] max_vec) throws Exception{
		int[] res_vec = new int[cur_vec.length];
		int carry = 1;
		for(int i = cur_vec.length - 1; i >= 0; i--){
			res_vec[i] = cur_vec[i] + carry;
			if(res_vec[i] > maximum_vec[i]){ 
				carry = 1;
				res_vec[i] = minimum_vec[i];
			}else{
				carry = 0;
			}
		}
		if(carry == 1 || Arrays.equals(res_vec,max_vec)){
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
