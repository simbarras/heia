package ch.epfl.general_libraries.traffic;

import ch.epfl.general_libraries.clazzes.ConstructorDef;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.simulation.Time;

public class Rate implements Cloneable {
	
	public static class GigabitPerS extends Rate {
		public GigabitPerS(double load) {
			super(1, 1/(load*1000));
			timeUnit = Time.S;
			dataUnit = DataSize.GBIT;
		}
	}
	
	public static class GBPerS extends Rate {
		public GBPerS(double load) {
			super(1, 1/(load*8000));
			timeUnit = Time.S;
			dataUnit = DataSize.GBYTE;			
		}
	}
	
	private final static Time REFTIME = new Time(1,Time.MCS);
	private final static DataSize REFSIZE = new DataSize(1,DataSize.BIT);	

	public static Rate NULL_RATE = new Rate(0f, DataSize.MBIT,Time.S);
	public static Rate INFINITE_RATE = new Rate(Float.POSITIVE_INFINITY, DataSize.MBIT, Time.S);
	public static Rate ONE_GBIT_S = new Rate(1f, DataSize.GBIT, Time.S);
	

	// Reference units are bits/mcs

	private float rate;
	protected String timeUnit;
	protected DataSize.Unit dataUnit;

	private Rate() {}
	
	private Rate(double value) { // 6 --> 0
		this(value, DataSize.BIT, Time.MCS);
	}
	
	public Rate(@ParamName(name="Bits") int bits, @ParamName(name="per microsec") double timeMCS) {  // 5 --> 6 --> 0
		this((double)bits/timeMCS);	
	}	
	
	public Rate(@ParamName(name="Bits") long bits, @ParamName(name="per microsec") double timeMCS) { // 5b --> 6 --> 0	
		this((double)bits/timeMCS);	
	}
	
	@ConstructorDef(ignore=true)
	public Rate(float value, String dataUnit, String timeUnit) { // 4 --> 0
		this(value, DataSize.parseUnit(dataUnit), timeUnit);
	}
	@ConstructorDef(ignore=true)	
	public Rate(double value, String dataUnit, String timeUnit) { // 4b --> 0
		this(value, DataSize.parseUnit(dataUnit), timeUnit);
	}	
	@ConstructorDef(ignore=true)	
	public Rate(DataSize data, Time t) {  // 3 --> 0
		this((double)data.getBits()/t.getMicroseconds(), data.getUnit(), t.getUnit());
	}
	@ConstructorDef(ignore=true)	
	public Rate(float value, DataSize.Unit dataUnit, String timeUnit) {  // 0b --> 0
		this((double)value, dataUnit, timeUnit);
	}			
	@ConstructorDef(ignore=true)
	public Rate(double value, DataSize.Unit dataUnit, String timeUnit) {  // 0
		this.dataUnit = dataUnit;
		this.timeUnit = timeUnit;
		Time t = new Time(1,timeUnit);
		double bits = DataSize.getBits(value, dataUnit);
		this.rate = (float)(bits /t.getMicroseconds());
	}









	@Override
	public Object clone() {
		return thisRate();
	}

	public Rate thisRate() {
		Rate new_ = new Rate();
		new_.rate = this.rate;
		new_.timeUnit = new String(this.timeUnit);
		new_.dataUnit = this.dataUnit;
		return new_;
	}		

	public Rate multiply(float factor) {
		Rate copy = (Rate) this.clone();
		copy.rate *= factor;
		return copy;
	}

	public Rate multiply(double d) {
		Rate copy = (Rate) this.clone();
		copy.rate *= d;
		return copy;
	}

	public Rate divide(float factor) {
		return multiply(1f/factor);
	}
	
	public double getInGbitSeconds() {
		// since rate is expressed in bits/mcs or kbit/mls or mbit/s, we have to multiply by 0.001;
		return rate*0.001d;
	}
	
	public double getInBitsSeconds() {
		return rate/0.000001d;
	}

	public float divide(Rate rate_) {
		return this.rate/rate_.rate;
	}

	/**
	 * Returns the emission frequency of packet of the given size at the current rate (in herz)
	 */
	public float divide(DataSize size) {
		return (float)((this.rate*1e6) / size.getBits());
	}

	public Time getTime(DataSize dt) {
		float rep = dt.getBits()/this.rate;
		return new Time(rep, Time.MCS);
	}

	public float getRate() {
		return rate;
	}

	public float getRate(String timeUnit) {
		float aux = (float)((new Time(1, timeUnit)).getMicroseconds());
		return rate * aux;
	}

	public float getRate(DataSize.Unit dataUnit, String timeUnit) {
		float aux = (float)((new Time(1, timeUnit)).getMicroseconds());
		float aux2 = (new DataSize(1, dataUnit)).getBits();
		return rate * aux / aux2;
	}

	public float getRate(String dataUnit, String timeUnit) {
		return getRate(DataSize.parseUnit(dataUnit), timeUnit);
	}

	public DataSize getSize(Time time) {
		float aux = (float)(time.getMicroseconds() *rate);
		aux = Math.round(aux);
		return new DataSize(aux, DataSize.BIT);
	}
	
	public int getSizeBitsNS(double nano) {	
		return (int)(rate*(nano/1000d));
	}

	public Time getTime(long bits) {
		return new Time(bits/rate);
	}

	public Time getTime(int bits) {
		return new Time((double)bits/rate);
	}
	
	public double getTimeNS(int sizeInBits) {
		return (1000*sizeInBits/rate);
	}	

	public void setTimeUnit(String s) {
		this.timeUnit = s;
	}

	public void setDataSizeUnit(String s) {
		this.dataUnit = DataSize.parseUnit(s);
	}

	public void setDataSizeUnit(DataSize.Unit u) {
		this.dataUnit = u;
	}

	@Override
	public String toString() {
		double rt = REFTIME.getLengthInUnit(timeUnit);
		double rs = REFSIZE.getLengthInUnit(dataUnit);
		return (rate/rt)*rs + " " + dataUnit + "/" + timeUnit;
	}


}
