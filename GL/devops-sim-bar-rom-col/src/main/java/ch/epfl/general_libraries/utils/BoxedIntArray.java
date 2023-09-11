package ch.epfl.general_libraries.utils;

import cern.colt.Arrays;

public class BoxedIntArray {

	public int[] array;

	public int[] copyArray() {
		int[] copy = new int[array.length];
		System.arraycopy(array, 0, copy, 0, array.length);
		return copy;
	}
	
	public void setArray(int[] maxDigits) {
		if (array == null || array.length != maxDigits.length) {
			array = new int[maxDigits.length];
		}
		System.arraycopy(maxDigits, 0, array, 0, maxDigits.length);
	}
	
	public String toString() {
		return Arrays.toString(array);
	}
}
