package Optimise;

import net.sourceforge.jswarm_pso.FitnessFunction;
import blueBlox.PDelleFast;

public class RowFitness extends FitnessFunction{
	public double evaluate(double position[]) { 
		PDelleFast p = new PDelleFast(position);
		double rows = p.runTest();
		return rows; 
	}
} 
