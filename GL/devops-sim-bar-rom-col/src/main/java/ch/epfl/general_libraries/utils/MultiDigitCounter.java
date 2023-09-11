package ch.epfl.general_libraries.utils;

import java.util.Iterator;
import java.util.Arrays;
import java.util.Stack;

public class MultiDigitCounter implements Iterable<BoxedIntArray> {
	
	private int[] maxDigits;
	private int count;
	private int[] current;
	
	private int nbDigits;
	
	private boolean justCycled = false;
	
	public MultiDigitCounter(int[] maxDigits) {
		this.maxDigits = maxDigits;
		this.nbDigits = maxDigits.length;
		this.current = new int[nbDigits];
	}
	
	public int[] getState() {
		return current;
	}
	
	public int[] increment() {
		if (nbDigits == 0) return new int[0];
		justCycled = false;
		count++;
		increment(nbDigits-1);
		return current;
	}
	
	public int increment(int d) {
		current[d]++;
		int toRet = d;
		if (current[d] == maxDigits[d]) {
			current[d] = 0;
			if (d > 0) {
				toRet = increment(d-1);
			}
			if (d == 0) {
				justCycled = true;
			}
		}
		return toRet;
	}
	
	public String toString() {
		return Arrays.toString(current);
	}
	
	public int getCount() {
		return count;
	}
	
	/**
	 * Increments the i-th digit and force digits placed more on the right to zero
	 * Returns the j-th dimension until which the increment propagates
	 * @param res
	 */
	public int incrementWithReset(int i) {
		int toRet = increment(i);
		if(i < current.length-1)
			Arrays.fill(current, i+1, current.length-1, 0);
		return toRet;
	}	
	
	public static void main(String[] args) {
	/*	MultiDigitCounter c = new MultiDigitCounter(new int[]{8, 10, 10});
		for (int i = 0 ; i < 1002 ; i++) {
			System.out.println(Arrays.toString(c.increment()));
		}
		System.out.println(Arrays.toString(c.getBases()));*/
		
		System.out.println(Arrays.toString(getCoords(457, 3)));
		System.out.println(getDecimalFromCoords(getCoords(457, 3),3));
	}
	
	public static int[] getCoords(int val, int base, int size) {
		int[] ret = new int[size];
		int[] coords = getCoords(val, base);
		System.arraycopy(coords, 0, ret, 0, Math.min(size, coords.length));
		return ret;
	}
	
	/**
	 * Return the coordinate
	 * @param val
	 * @param base
	 * @return
	 */
	public static int[] getCoords(int val, int base) {
		if (val < 0) throw new IllegalStateException("no neg numbers");
		Stack<Integer> c = new Stack<Integer>();
		int copy = val;
		int b = base;
		int b1 = 1;
		while (copy != 0) {
			int a = copy % b;
			c.push(a/b1);
			copy -= a;
			b *= base;
			b1 *= base;
		}
		int[] ret = new int[c.size()];
		b = 0;
		for (Integer i : c) {
			ret[b] = i;
			b++;
		}
		return ret;
	}
	
	public static int getDecimalFromCoords(int[] coords, int base) {
		int accum = 0;
		int b = 1;
		for (int i = 0 ; i < coords.length ; i++) {
			accum += coords[i] * b;
			b *= base;
		}
		return accum;
	}

	public int[] getBases() {
		if (nbDigits == 0) return new int[]{1};
		int[] bases = new int[nbDigits];
		bases[nbDigits-1] = 1;
		if (nbDigits == 1) return bases;
		for (int i = nbDigits-2 ; i >= 0 ; i--) {
			bases[i] = bases[i+1]*maxDigits[i+1];
		}
		return bases;
	}

	public short[] getStateShort() {
		short[] bases = new short[nbDigits];
		for (int i = 0 ; i < bases.length ; i++) {
			bases[i] = (short)current[i];
		}
		return bases;
	}

	@Override
	public Iterator<BoxedIntArray> iterator() {
		return new Iterator<BoxedIntArray>() {
			
			MultiDigitCounter intern;
			boolean hasNext;
			BoxedIntArray array;
			
			{
				intern = new MultiDigitCounter(maxDigits);
				hasNext = (MoreArrays.max(maxDigits) > 0);
				array = new BoxedIntArray();
			}

			@Override
			public boolean hasNext() {
				return hasNext;
			}

			@Override
			public BoxedIntArray next() {
				array.setArray(intern.current); 
				intern.increment();
				if (intern.justCycled) hasNext = false;
				return array;
			}

			@Override
			public void remove() {
			}
			
		};
	}







}
