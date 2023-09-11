package ch.epfl.javanco.utils;

public class NodeIndexManager {
	
	private boolean[] occupation;
	private int counter;

	public NodeIndexManager() {
		reset();
	}

	public void reset() {
		occupation = new boolean[1];
		counter = 0;
	}

	public int getUnused() {
		for (int i = 0 ; i < occupation.length ; i++) {
			if (occupation[i] == false) {
				occupation[i] = true;
				counter++;
				return i;
			}
		}
		boolean[] newOcc = new boolean[occupation.length+1];
		System.arraycopy(occupation, 0, newOcc, 0, occupation.length);
		newOcc[occupation.length] = true;
		counter++;
		occupation = newOcc;
		return occupation.length - 1;
	}


	public boolean hasGaps() {
		for (int i = 0 ; i < occupation.length ; i++) {
			if (occupation[i] == false) return true;
		}
		return false;
	}

	public int getNumberOfNodes() {
		return counter;
	}

	public void setAsUsed(int i) {
		if (occupation.length <= i) {
			boolean[] newOcc = new boolean[i+1];
			System.arraycopy(occupation, 0, newOcc, 0, occupation.length);
			occupation = newOcc;
		} else {
			if (occupation[i]) throw new IllegalStateException();			
		}
		occupation[i] = true;
		counter++;
	}

	public boolean isUsed(int i) {
		if (occupation.length > i) {
			return occupation[i];
		}
		return false;
	}

	public void recycle(int i) {
		occupation[i] = false;
		counter--;
	}

	public int getHighestIndex() {
		for (int i = occupation.length -1 ; i >= 0 ; i--) {
			if (occupation[i] == true) return i;
		}
		return -1;
	}
}

