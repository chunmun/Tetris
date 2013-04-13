package blueBlox;

import java.util.Random;
import java.util.Vector;

import test.PDelleFast;
import test.State;
import test.Test;

public class Tester {
	public static void main(String args[]){
		int threads = 2;
		Vector<Thread> th = new Vector<Thread>();
		for (int i = 0; i < threads; i++) {
			th.add(new Thread(new Test()));
		}

		for (int i = 0; i < threads; i++) {
			th.get(i).start();
		}
	}
}


class Test implements Runnable {
	@Override
	public void run() {
		State s = new State();
		PDelleFast p = new PDelleFast(new double[] {-1,1,-1,-1,-6,-2,0.25});
		while(!s.hasLost()) {
			s.makeMove(p.pickMove(s,s.legalMoves()));
		}
		System.out.println("You have completed "+s.getRowsCleared()+" rows.");	
	}
}