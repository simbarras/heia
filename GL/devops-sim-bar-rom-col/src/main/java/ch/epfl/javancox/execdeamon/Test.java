package ch.epfl.javancox.execdeamon;

public class Test {
	static int maxThreads = 2;
	static int n = 10;
	static int tasksPerThread = (n + maxThreads - 1) / maxThreads;
	static int nExtra = tasksPerThread * maxThreads - n;
	static int nFull = tasksPerThread * (maxThreads - nExtra);
	static int start = 0;

	public static void main(String[] args) { {

		while (start < nFull) {
			int end = start + tasksPerThread - 1;
			System.out.printf("%1$d,%2$d%n", start, end);
			start = end + 1;
		}
		while (start < n) {
			int end = start + tasksPerThread - 2;
			System.out.printf("%1$d,%2$d%n", start, end);
			start = end + 1;
		}
	}

	}

}
