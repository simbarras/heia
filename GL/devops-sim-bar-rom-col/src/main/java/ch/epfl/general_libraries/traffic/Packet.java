package ch.epfl.general_libraries.traffic;

import ch.epfl.general_libraries.simulation.Time;

public class Packet {
	long size;
	Time timestamp;

	public Packet(long size, Time timestamp) {
		this.size = size;
		this.timestamp = timestamp.thisTime();
	}

	public void setTime(Time timestamp) {
		this.timestamp = timestamp;
	}

	public Time getTime() {
		return timestamp;
	}

	/**
	 * Return size in bits
	 */
	public long getSize() {
		return size;
	}

	@Override
	public String toString() {
		return "p["+size+"/timeS:" + timestamp + "]";
	}

}
