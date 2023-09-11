package ch.epfl.general_libraries.traffic;

import ch.epfl.general_libraries.logging.Logger;
import ch.epfl.general_libraries.simulation.Time;

public class DataSize implements Cloneable {

	static class Unit {
		String text;
		Unit previous;
		Unit next;

		Unit(String text) {
			this.text = text;
		}

		@Override
		public String toString() {
			return text;
		}
	}

	public static final Unit BIT;
	public static final Unit BYTE;
	public static final Unit KBIT;
	public static final Unit KBYTE;
	public static final Unit MBIT;
	public static final Unit MBYTE;
	public static final Unit GBIT;
	public static final Unit GBYTE;

	private static final Unit[] UNITS;

	static {
		BIT = new Unit("bit");
		BYTE = new Unit("byte");
		KBIT = new Unit("kbit");
		KBYTE = new Unit("kbyte");
		MBIT = new Unit("mbit");
		MBYTE = new Unit("mbyte");
		GBIT = new Unit("gbit");
		GBYTE = new Unit("gbyte");

		UNITS = new Unit[]{BIT, BYTE, KBIT, KBYTE, MBIT, MBYTE, GBIT, GBYTE};

		BIT.previous = null;
		BIT.next = KBIT;
		BYTE.previous = null;
		BYTE.next = KBYTE;
		KBIT.previous = BIT;
		KBIT.next = MBIT;
		KBYTE.previous = BYTE;
		KBYTE.next = MBYTE;
		MBIT.previous = KBIT;
		MBIT.next = GBIT;
		MBYTE.previous = KBYTE;
		MBYTE.next = GBYTE;
		GBIT.previous = MBIT;
		GBIT.next = null;
		GBYTE.previous = MBYTE;
		GBYTE.next = null;
	}

	private static final Logger logger = new Logger(DataSize.class);

	public static DataSize NULL_DATASIZE = new DataSize(0,BIT.text);

	private Unit unit;
	private double size;

	public static Unit parseUnit(String t) {
		for (Unit u : UNITS) {
			if (u.text.equals(t)) {
				return u;
			}
		}
		return null;
	}

	private DataSize() {
	}

	public DataSize(int size) {
		this(size, (Unit)null);
	}

	public DataSize(long size) {
		this(size, (Unit)null);
	}

	public DataSize(long size, String unit) {
		this((double)size, unit);
	}

	public DataSize(int size, String unit) {
		this((double)size, unit);
	}

	public DataSize(float size, String unit) {
		this((double)size, unit);
	}

	public DataSize(double size, String unit) {
		this(size, parseUnit(unit));
	}

	public DataSize(double size, Unit unit) {
		// reference is bit
		if (size < 0) {
			throw new IllegalArgumentException("Negative sizes are not allowed");
		}
		if (unit != null) {
			if (unit.equals(DataSize.BIT)) {
				if ((size - Math.round(size)) != 0) {
					throw new IllegalArgumentException("DataSize with non integer values and bit unit not allowed (arg : " + size+")");
				}
			}
			else if (unit.equals(DataSize.BYTE)) {
				if ((size - Math.round(size)) != 0) {
					throw new IllegalArgumentException("DataSize with non integer values and bit unit not allowed (arg : " + size+")");
				}
			}
			this.size = getBits(size, unit);
			this.unit = unit;
		} else {
			this.size = size;
			this.unit = DataSize.BIT;
		}
	}
	
	public static double getBits(double value, Unit unit) {
		double size_ = 0;
		if (unit.equals(DataSize.BIT)) {
			size_ = value;
		}
		else if (unit.equals(DataSize.BYTE)) {
			size_ = value * 8;
		}
		else if (unit.equals(DataSize.KBIT)) {
			size_ = value * 1000;
		}
		else if (unit.equals(DataSize.KBYTE)) {
			size_ = value * 8 * 1000;
		}
		else if (unit.equals(DataSize.MBIT)) {
			size_ = value * 1000000;
		}
		else if (unit.equals(DataSize.MBYTE)) {
			size_ = value * 8 * 1000 * 1000;
		}
		else if (unit.equals(DataSize.GBIT)) {
			size_ = value * 1000000000;
		}
		else if (unit.equals(DataSize.GBYTE)) {
			size_ = value * 8 * 1000 *1000 *1000;
		}
		else {
			IllegalArgumentException e = new IllegalArgumentException(unit + " is not a recognized type of unit");
			logger.error("Unrecognized type of data unit", e);
			throw e;
		}
		return size_;		
	}

	@Override
	public String toString() {
		double ret = size;//getLength(this.unit);
		Unit u = null;
		if (unit != null) {
			u = unit;
			while(u.previous != null) {
				ret = ret / 1000d;
				u = u.previous;
			}
		} else {
			u = BIT;
			if (ret > 1000) {
				while (ret > 1000 && u != null) {
					ret = ret / 1000d;
					u = u.next;
				}
			} else {
				while (ret < 1 && u != null) {
					ret = ret * 1000;
					u = u.previous;		
				}
			}
		}



		String val = ret + "";
		if ((val.length() > 7) && (ret < 10000)) {
			val = val.substring(0,5);
		}
		if (unit != null) {
			return val + " " + unit;
		} else {
			return val + " " + u;
		}
	}

	public String toString(String unit) {
		return getLengthInUnit(unit) + unit;
	}

	public double getLengthInUnit(String s) {
		return getLengthInUnit(parseUnit(s));
	}

	public double getLengthInUnit(Unit unit) {
		return getLength(this.size, unit);
	}

	public static double getLength(double _size, Unit unit) {
		if (unit == DataSize.BIT) {
			return _size;
		}
		else if (unit == DataSize.BYTE) {
			return _size/8f;
		}
		else if (unit == DataSize.KBIT) {
			return _size/1000f;
		}
		else if (unit == DataSize.KBYTE) {
			return _size/(8f * 1000f);
		}
		else if (unit == DataSize.MBIT) {
			return _size/1000000f;
		}
		else if (unit == DataSize.MBYTE) {
			return _size/(8f *1000f * 1000f);
		}
		else if (unit == DataSize.GBIT) {
			return _size/1000000000f;
		}
		else if (unit == DataSize.GBYTE) {
			return _size/(8f * 1000f * 1000f * 1000f);
		} else {
			IllegalStateException e =  new IllegalStateException("Should not be here");
			logger.error("Should never be here", e);
			throw e;
		}
	}

	public long getBits() {
		return Math.round(size);
	}

	public long getBytes() {
		return Math.round(size / 8);
	}

	public double getKilobits() {
		return this.size / 1000;
	}

	public double getKilobytes() {
		return this.size / (8 *1000);
	}

	public double getMegabits() {
		return this.size / 1000000;
	}

	public double getMegabytes() {
		return this.size / (8 *1000 * 1000);
	}

	public double getGigabits() {
		return this.size / 1000000000;
	}

	public double getGigabytes() {
		return this.size / (8 *1000*1000*1000);
	}

	public DataSize plus(DataSize other ) {
		DataSize copy = new DataSize();
		copy.size = this.size + other.size;
		copy.unit = this.unit;
		return copy;
	}
	
	public void plusSame(DataSize other) {
		this.size += other.size;
	}

	public DataSize minus(DataSize other) {
		DataSize copy = new DataSize();
		copy.size = this.size - other.size;
		copy.unit = this.unit;
		return copy;
	}

	public DataSize divided(double factor) {
		DataSize copy = new DataSize();
		copy.size =	this.size / factor;
		copy.unit = this.unit;
		return copy;
	}
	
	public Time divided(Rate rate) {
		return new Time(this.size/(float)rate.getRate());
	}

	public double divided(DataSize other) {
		return this.size / other.size;
	}

	public DataSize times(double factor) {
		DataSize copy = new DataSize();
		copy.size = this.size * factor;
		copy.unit = this.unit;
		return copy;
	}

	@Override
	public Object clone() {
		DataSize copy = new DataSize();
		copy.size = this.size;
		if (this.unit != null) {
			copy.unit = this.unit;
		}
		return copy;
	}

	public Unit getUnit() {
		return this.unit;
	}

	public void setUnit(Unit un) {
		this.unit = un;
	}


}

