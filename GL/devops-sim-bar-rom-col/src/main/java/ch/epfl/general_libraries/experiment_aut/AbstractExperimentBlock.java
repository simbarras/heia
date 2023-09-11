package ch.epfl.general_libraries.experiment_aut;

import java.util.Map;

import ch.epfl.general_libraries.utils.SimpleMap;

public abstract class AbstractExperimentBlock implements ExperimentBlock {

	public static Map<String, String> getEmptyMap() {
		return new SimpleMap<String, String>(0);
	}

	public static Map<String, String> getNewMap() {
		return new SimpleMap<String, String>(1);
	}

	public static Map<String, String> getNewMap(int i) {
		return new SimpleMap<String, String>(i);
	}

	public static Map<String, String> getMap(String[] ... params) {
		SimpleMap<String, String> map = new SimpleMap<String, String>(params.length);
		for (String[] tab : params) {
			map.put(tab[0], tab[1]);
		}
		return map;
	}

	public static Map<String, String> getMap(String ... params) {
		if (params.length % 2 != 0) {
			throw new IllegalArgumentException("Number of argument must be pair");
		}
		SimpleMap<String, String> map = new SimpleMap<String, String>(params.length/2);
		for (int i = 0 ; i < params.length / 2 ; i++) {
			map.put(params[2*i], params[(2*i)+1]);
		}
		return map;
	}

/*	public static PRNStream getDefaultStream() {
		return PRNStream.getDefaultStream(0);
	}*/

	public static int[] getIntArray(int min, int max) {
		int[] a = new int[max-min+1];
		for (int i = 0 ; i < a.length ; i++) {
			a[i] = min+i;
		}
		return a;
	}
	
	public static Integer[] getIntegerArray(int min, int max) {
		Integer[] a = new Integer[max-min+1];
		for (int i = 0 ; i < a.length ; i++) {
			a[i] = min+i;
		}
		return a;
	}	

	public static int[] getIntArray(int min, int max, int coeff) {
		int[] a = new int[max-min+1];
		for (int i = 0 ; i < a.length ; i++) {
			a[i] = coeff*(min+i);
		}
		return a;
	}

	public static int[] getIntArray(int min, int max, int incr, int coeff) {
		int[] a = new int[((max-min)/incr)+1];
		for (int i = 0 ; i < a.length ; i++) {
			a[i] = coeff*(min+(i*incr));
		}
		return a;
	}

	public static double[] getDoubleArray(double min,  double max, double incr) {
		double[] a = new double[(int)Math.floor((max-min)/incr)+1];
		for (int i = 0 ; i < a.length ; i++) {
			a[i] = (min+(i*incr));
		}
		return a;
	}

	public static float[] getFloatArray(float min,  float max, float incr) {
		float[] a = new float[(int)Math.floor((max-min)/incr)+1];
		for (int i = 0 ; i < a.length ; i++) {
			a[i] = (min+(i*incr));
		}
		return a;
	}
}
