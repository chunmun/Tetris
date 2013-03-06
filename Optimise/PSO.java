package Optimise;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

import net.sourceforge.jswarm_pso.Swarm;

public class PSO{
	public volatile static Vector<String> results = new Vector<String>();

	public PSO(){
	}

	public static void main(String args[]) throws IOException{
		double HARD_MIN = 0;
		double HARD_MAX = 10;
		int threadNum = 4;
		int runNum = 10;

		Thread[] pool = new Thread[threadNum];

		FileWriter output = new FileWriter(new File("output.txt"), true);	
		
		for(int i = 0; i < threadNum; i++){
			pool[i] = new Thread(new Simulation(HARD_MIN, HARD_MAX, runNum, output, "Simulator-"+i));
			pool[i].start();
		}

		boolean someAlive = true;
		while(someAlive){
			someAlive = false;
			for(int i = 0; i < threadNum; i++){
				if(pool[i].isAlive()){
					someAlive = true;
					break;
				}
			}

			try{
				Thread.sleep(5000);
			} catch (InterruptedException e){
				e.printStackTrace();
			}
		}
		System.out.println("AllComplete");
		System.out.println("============================ RESULTS =============================");

		output.close();
	}
}
class Simulation implements Runnable{
	double min;
	double max;
	int runs;
	FileWriter output;
	String name;

	public Simulation(double min, double max, int runs, FileWriter output, String name){
		this.min = min;
		this.max = max;
		this.runs = runs;
		this.output = output;
		this.name = name;
	}

	public void run(){
		for(int i = 0; i < runs; i++){
			String res = test(i);
			synchronized(output){
				try {
					output.write(System.getProperty("line.separator") + res);
					output.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			System.out.println("*************************************");
			System.out.println(name + " completes " + (i+1) + " / " + runs + " runs");
			System.out.println("*************************************");
		}
	}

	public String test(int runNum){
		Swarm swarm = new Swarm(Swarm.DEFAULT_NUMBER_OF_PARTICLES, new PlayerParticle(), new RowFitness());
		// Set position (and velocity) constraints. 
		// i.e.: where to look for solutions
		swarm.setMaxPosition(Math.random() * max);
		swarm.setMinPosition(Math.random() * min);
		swarm.setGlobalIncrement(0.1);
		//		swarm.setInertia(0.5);
		swarm.setParticleIncrement(0.1);

		// Optimize a few time
		for( int i = 0; i < 20; i++ ){
			swarm.evolve();
			System.out.println(name + " : Run " + runNum + ", Stage " + i);
		}
		// Print en results
		//		System.out.println(name + " => " + swarm.toStringStats());
		String best = swarm.getBestFitness()+":"+Arrays.toString(swarm.getBestPosition());
		System.out.println(best);
		return best;
	}
}
