package ch.epfl.general_libraries.utils;

import cern.colt.Arrays;

public class DoubleAccumulator {

	private double[] val;
	
	public DoubleAccumulator(int size) {
		val = new double[size];
	}

	public DoubleAccumulator() {
		val = new double[1];
	}

	public void increment() {
		val[0]++;
	}

	public void decrement() {
		val[0]--;
	}

	public double getValue() {
		return val[0];
	}
	
	public double getValue(int index) {
		return val[index];
	}
	
	public void add(double d) {
		val[0] += d;
	}
	
	public void add(double d, int index) {
		val[index] += d;
	}

	@Override
	public String toString() {
		return "value="+Arrays.toString(val);
	}	
	
}
