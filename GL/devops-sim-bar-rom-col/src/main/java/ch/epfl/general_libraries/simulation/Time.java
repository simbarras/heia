package ch.epfl.general_libraries.simulation;
import java.io.Serializable;

import ch.epfl.general_libraries.logging.Logger;


public class Time implements Cloneable, Comparable<Time>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String PS = "ps";
	public static final String NS = "ns";
	public static final String MCS = "mcs";
	public static final String MLS = "mls";
	public static final String S = "s";

	public static final Time INFINITE_TIME = new Time(Double.POSITIVE_INFINITY);
	public static final Time ZERO_TIME = new Time(0);
	public static final Time SECOND = new Time(1, S);
	public static final Time NANOSECOND = new Time(1, NS);
	public static final Time MICROSECOND = new Time(1, MCS);
	public static final Time MILLISECOND = new Time(1, MLS);



	private static final Logger logger = new Logger(Time.class);


	private String __unit;
	// This is always referring to nanoseconds
	private double length;

	private Time() {
	}

	public Time(float length) {
		this((double)length);
	}

	public Time(double length) {
		this(length, MCS);
	}

	public Time(float length, String unit) {
		this((double)length, unit);
	}

	public Time(double length, String unit) {
		// reference is microseconds
		if (unit.equals(PS)) {
			this.length = length / 1000;
		}
		else if (unit.equals(NS)) {
			this.length = length;
		}
		else if (unit.equals(MCS)) {
			this.length = length * 1000;
		}
		else if (unit.equals(MLS)) {
			this.length = length * 1000000;
		}
		else if (unit.equals(S)) {
			this.length = length * 1000000000;
		}
		else {
			IllegalArgumentException e = new IllegalArgumentException(unit + " is not a recognized type of unit");
			logger.error("Unrecognized type of time unit", e);
			throw e;
		}
		this.__unit = unit;
	}

	public void setUnit(String s) {
		__unit = s;
	}

	protected void setInternal(float f, String s) {
		this.length = f;
		this.__unit = s;
	}

	protected void setInternal(Time t) {
		this.length = t.length;
		this.__unit = t.__unit;
	}

	@Override
	public String toString() {
		double ret = getLength(this.length, this.__unit);
		String val = ret + "";
		/*	if ((val.length() > 7) && (ret < 10000)) {
			val = val.substring(0,5);
		}*/
		return val + __unit;
	}

	public double getValue() {
		return this.length;
	}

	public String toString(String unit) {
		return getLengthInUnit(unit) +" "+ unit;
	}
	
	public double getLengthInUnit(String unit) {
		return getLength(this.length, unit);
	}

	public static double getLength(double val, String unit) {
		if (unit.equals(PS)) {
			return val*1000;
		}
		else if (unit.equals(NS)) {
			return val;
		}
		else if (unit.equals(MCS)) {
			return val/1000d;
		}
		else if (unit.equals(MLS)) {
			return val/1000000d;
		}
		else if (unit.equals(S)) {
			return val/1000000000d;
		} else {
			IllegalStateException e =  new IllegalStateException("Should not be here");
			//			logger.error("Should never be here", e);
			throw e;
		}
	}

	public double getCorrespondingFrequency() {
		return 1e9 / this.length;
	}

/*	public double getInverseLength(String unit) {
		return getInverseLength(1, unit);
	}*/

	public static double getInverseLength(double val, String unit) {
		if (unit.equals(PS)) {
			return val/1000d;
		}
		else if (unit.equals(NS)) {
			return val;
		}
		else if (unit.equals(MCS)) {
			return val * 1000d;
		}
		else if (unit.equals(MLS)) {
			return val*1000000d;
		}
		else if (unit.equals(S)) {
			return val*1000000000d;
		} else {
			IllegalStateException e =  new IllegalStateException("Should not be here");
			//			logger.error("Should never be here", e);
			throw e;
		}
	}

	public double getSeconds() {
		return this.length / 1000000000d;
	}

	public double getMilliseconds() {
		return this.length / 1000000d;
	}

	public double getMicroseconds() {
		return this.length / 1000d;
	}

	public double getNanoseconds() {
		return this.length;
	}

	public double getPicoseconds() {
		return this.length *1000;
	}

	public Time plus(Time other ) {
		Time clone_ = this.thisTime();
		clone_.length += other.length;
		return clone_;
	}
	
	public Time plusNS(double ns) {
		Time t = new Time((this.getNanoseconds() + ns));
		t.__unit = this.__unit;
		return t;
	}
	
	public void setNanoseconds(double d) {
		length = d;
	}

	public void plusSame(Time other) {
		this.length += other.length;
	}
	
	public void plusSameNS(double ns) {
		this.length += ns;
	}

	public Time minus(Time other) {
		Time clone_ = this.thisTime();
		clone_.length -= other.length;
		return clone_;
	}

	public void minusSame(Time other) {
		this.length -= other.length;
	}

	public Time multiply(float factor){
		Time clone_ = this.thisTime();
		clone_.length *= factor;
		return clone_;
	}

	public Time divided(float factor) {
		Time clone_ = this.thisTime();
		clone_.length /= factor;
		return clone_;
	}

	public double divided(Time other) {
		return this.length / other.length;
	}

	public boolean isStrictlySmallerThan(Time t) {
		return (this.length < t.length);
	}

	public boolean isSmallerThan(Time t) {
		return (this.length <= t.length);
	}

	public boolean isSmallerThan(double f) {
		return (this.length <= f);
	}

	public boolean isStrictlyGreaterThan(Time t) {
		return (this.length > t.length);
	}

	public boolean isGreaterThan(Time t) {
		return (this.length > t.length);
	}

	public boolean isZero() {
		return (this.length == 0);
	}

	public Time thisTime() {
		Time t = new Time();
		t.length = this.length;
		t.__unit = this.__unit;
		return t;
	}

	public int compareTo(Time t) {
		if (length < t.length) {
			return -1;
		}
		if (length > t.length) {
			return 1;
		}
		return 0;
	}

	public boolean equalsTo(Time t){
		/*Oscar*/
		if(this.length == t.length){
			return true;
		}
		return false;
	}

	public boolean isStrictlyPositive() {
		return (this.length > 0);
	}

	public boolean isStrictlyNegative() {
		return (this.length < 0);
	}

	public boolean isPositive() {
		return (this.length >= 0);
	}

	public boolean isNegative() {
		return (this.length <= 0);
	}

	public void reset() {
		this.length = 0;
	}

	public String getUnit() {
		return this.__unit;
	}

	public String getAdequateUnit() {
		if (this.length <= 0.01) {
			return PS;
		} else if (this.length <= 10) {
			return NS;
		} else if (this.length <= 10000) {
			return MCS;
		} else if (this.length <= 10000000) {
			return MLS;
		} else {
			return S;
		}
	}

	public double getAdequateUnitValue() {
		if (this.length <= 0.01) {
			return length * 1e3d;
		} else if (this.length <= 10) {
			return length;
		} else if (this.length <= 10000) {
			return length * 1e-3;
		} else if (this.length <= 10000000) {
			return length * 1e-6;
		} else {
			return length * 1e-9;
		}
	}


}
