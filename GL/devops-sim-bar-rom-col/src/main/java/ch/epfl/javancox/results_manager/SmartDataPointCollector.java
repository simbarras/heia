package ch.epfl.javancox.results_manager;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

import ch.epfl.general_libraries.io.ObjectStreamHelper;
import ch.epfl.general_libraries.math.StatFunctions;
import ch.epfl.general_libraries.results.AbstractInOutDataManager;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataRetrievalOptions;
import ch.epfl.general_libraries.results.Criterium;
import ch.epfl.general_libraries.results.CriteriumSet;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.DataSeries;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.results.Property;
import ch.epfl.general_libraries.results.ResultProperty;
import ch.epfl.general_libraries.traffic.DataSize;
import ch.epfl.general_libraries.utils.Pair;
import ch.epfl.general_libraries.utils.PairList;
import ch.epfl.general_libraries.utils.SimpleMap;
import ch.epfl.general_libraries.utils.SimpleSet;

public class SmartDataPointCollector extends AbstractInOutDataManager implements SaveAndLoadAble, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<InternalExecution> execList;
	TreeMap<String, InternalProperty> propList;

	final static String WC = "##";
	final static String NULL = "null";

	public static boolean usingDpHasRef = false;

	public SmartDataPointCollector() {
		execList = new ArrayList<InternalExecution>();
		propList = new TreeMap<String, InternalProperty>(String.CASE_INSENSITIVE_ORDER);
	}
	
	public InternalProperty getInternalProperty(String key) {
		synchronized (propList) {
			return propList.get(key);
		}
	}
	
	public void addInternalProperty(String key, InternalProperty prop) {
		synchronized (propList) {
			propList.put(key, prop);
		}
	}
	
	@Override
	public void addDataPoint(DataPoint dp) {
		Execution e = new Execution();
		e.addDataPoint(dp);
		this.addExecution(e);
	}
	
	public Map<String, InternalProperty> getPropListCopy() {
		synchronized (propList) {
			SimpleMap<String, InternalProperty> gs = new SimpleMap<String, InternalProperty>(propList.size());
			for (Map.Entry<String, InternalProperty> s : propList.entrySet()) {
				gs.put(s.getKey(), s.getValue());
			}
			return gs;
		}
	}	
	
	public ArrayList<InternalExecution> getExecListCopy() {
		synchronized (execList) {
			@SuppressWarnings("unchecked")
			ArrayList<InternalExecution> copy = (ArrayList<InternalExecution>)execList.clone();
			return copy;
		}
	}

	public void saveToFile(java.io.File f) {
		ObjectStreamHelper.writeObject(new Object[]{execList, propList}, f);
	}

	@SuppressWarnings("unchecked")
	public void loadFromFile(java.io.File f) {
		Object[] s = ObjectStreamHelper.readObject(f);
		execList = (ArrayList<InternalExecution>)s[0];
		propList = (TreeMap<String, InternalProperty>)s[1];
	}

	public void addExecution(Execution e) {
		if (e != null) {
			synchronized (execList) {
				InternalExecution ie = new InternalExecution(this, e);				
				execList.add(ie);
			}
		} else {
			throw new NullPointerException("A null execution has been submitted to the DB");
		}
	}

	public float confidenceInterval(CriteriumSet cs, String metric) {

		cs.setPossibleValues(this);

		float max = Float.NEGATIVE_INFINITY;

		CriteriumSet.CriteriaIterator ite = cs.criteriaIterator();
		for (List<PairList<String, String>> col : ite) {
			ArrayList<Float> list = new ArrayList<Float>();
			for (InternalExecution exec : getExecListCopy()) {
				for (InternalDataPoint dp : exec.dataPoints) {
					if (dp.isDefinedForProperty(metric) == false) {
						continue;
					}

					if (filterCrit(dp, col, null)) {
						float f = Float.parseFloat(dp.getValue(metric));
						list.add(f);
					}
				}
			}
			if (list.size() > 4) {
				float f = StatFunctions.getRelativeConfidenceInterval(list, 0.95f);

				System.out.println(col.toString() + "    " + list);

				max = Math.max(max, f);
			} else if (list.size() > 0) {
				max = Float.POSITIVE_INFINITY;
			}
		}
		return Math.abs(max);
	}

	public void clear() {
		execList = new ArrayList<InternalExecution>();
		propList = new TreeMap<String, InternalProperty>(String.CASE_INSENSITIVE_ORDER);
	}

	public List<String> getMetrics() {
		List<String> met = new ArrayList<String>();
		for (InternalProperty ip : propList.values()) {
			if (ip.isNumbersOnly()) {
				met.add(ip.name);
			}
		}
		return met;
	}

	public Set<String> getParameters() {
		return propList.keySet();
	}

	public boolean isInput(String param) {
		if (param == null) {
			return false;
		}
		try {
			return propList.get(param).isResult() == false;
		}
		catch (NullPointerException e) {
			return false;
		}
	}


	@SuppressWarnings("unchecked")
	public Set<String> getPossibleValuesOfGivenProperty(String property) {
		synchronized (execList) {
			InternalProperty ip = propList.get(property);
			if (ip != null) {
				return ip.getValues();
			}
			return SimpleSet.EMPTY_SET;
		}
	}

/*	public List<Pair<String, TreeSet<String>>> getVariableProperties(DataRetrievalOptions p, TreeSet<Integer> expIDs) {
		return null;
	}*/

	public AbstractResultsManager getAssociatedResultManager() {
		return this;
	}

	public boolean useResults() {
		return false;
	}

	private boolean filter(InternalDataPoint dp, Map<String, List<String>> p) {
		for (Map.Entry<String, List<String>> prop : p.entrySet()) {
			if (prop.getValue().contains(WC)) {
				if (!dp.isDefinedForProperty(prop.getKey())) {
					continue;
				}
			} else {
				if (!dp.isDefinedForProperty(prop.getKey())) {
					return false;
				}
			}
			if (!prop.getValue().contains(dp.getValue(prop.getKey()))) {
				return false;
			}
		}
		return true;
	}

	private boolean filterCrit(InternalDataPoint dp, List<PairList<String, String>> col, Map<String, List<String>> pw) {
		for (PairList<String, String> pl : col) {
			for (Pair<String, String> s : pl) {
				if (s.getSecond() == CONSTANT) {
					continue;
				}
				if (s.getSecond() == WC) {
					if (dp.isDefinedForProperty(s.getFirst())) {
						return false;
					}
					if (pw != null) {
						for (Map.Entry<String, List<String>> pwc : pw.entrySet()) {
							if (s.getFirst().equals(pwc.getKey())) {
								if (!pwc.getValue().contains(WC)) {
									return false;
								}
							}
						}
					}
				} else {
					String ipv = dp.getValue(s.getFirst());
					if (ipv == null) {
						return false;
					}
					if (!ipv.equals(s.getSecond())) {
						return false;
					}
				}
			}
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public Vector[] getVariableAndConstantPropertiesForGivenMetric(String s) {
		Vector<String> toRet1 = null;
		try {
			toRet1 = new Vector<String>(propList.get(s).relatedProperties.keySet());
		}
		catch (NullPointerException e) {
			System.out.println("hum");
			return new Vector[]{};
		}
		
	//	Vector<String> toRemove = new Vector<String>(toRet1.size());
	//	Vector<String> valOfToRemove = new Vector<String>(toRet1.size());
		TreeMap<String, String> constants = new TreeMap<String, String>();
		
		ArrayList<InternalDataPoint> list = new ArrayList<InternalDataPoint>(1000);
		for (InternalExecution exec : getExecListCopy()) {
			for (InternalDataPoint dp : exec.dataPoints) {
				if (dp.isDefinedForProperty(s) == false) {
					continue;
				} else {
					list.add(dp);
				}
			}
		}
		if (list.size() == 0) return new Vector[]{toRet1, new Vector(), null};
		InternalDataPoint[] array = list.toArray(new InternalDataPoint[list.size()]);
		
		for (String stri : toRet1) {
			boolean rem = true;
			String ref = array[0].getValue(stri);
			for (int i = 1 ; i < array.length ; i++) {
				String t = array[i].getValue(stri);	
				if (t == null || !t.equals(ref)) {
					rem = false;
					break;
				} else {
					continue;
				}
			}
			if (rem) {
				constants.put(stri, ref);
			}
		}
		toRet1.removeAll(constants.keySet());
		
		java.util.Collections.sort(toRet1);
		
		Vector<String> s1 = new Vector();
		Vector<String> s2 = new Vector();		
		for (Map.Entry<String, String> ent : constants.entrySet()) {
			s1.add(ent.getKey());
			s2.add(ent.getValue());
		}
		

		
		return new Vector[]{toRet1, s1, s2};
	}



	public List<DataSeries> getChartValues(DataRetrievalOptions p, String methodName) {

		String xAxis = p.xAxisProperty;
		CriteriumSet cs = p.getCriteriumSet();
		Map<String, List<String>> filters = p.getFilters();

		return getChartValues(xAxis, cs, filters, methodName);
	}

	public List<DataSeries> getChartValues(String xAxis, CriteriumSet cs, String methodName) {
		return getChartValues(xAxis, cs, null, methodName);
	}

	public synchronized List<DataSeries> getChartValues(String xAxis, CriteriumSet cs, Map<String, List<String>> filters, String methodName) {
		
		boolean noXAxis = false;

		if (xAxis.equals("")) {
			boolean check = false;
			// checking if CONSTANT is the unique criterium
			for (List<Criterium> lc : cs) {
				for (Criterium c : lc) {
					if (c.getName().equals(CONSTANT)) {
						check = true;
						noXAxis = true;
						break;
					}
				}
			}
			if (!check) {
				return new ArrayList<DataSeries>(0);				
			}
		} else {
			boolean def = false;
			InternalProperty xAxisProp = propList.get(xAxis);
			if (xAxisProp == null) return new ArrayList<DataSeries>(0);
			for (List<Criterium> lc : cs) {
				for (Criterium c : lc) {
					if (xAxisProp.relatedProperties.get(c.getName()) != null || c.getName().equals(CONSTANT)) {
						def = true;
						break;
					}
				}
			}
			if (!def) {
				return new ArrayList<DataSeries>(0);
			}
		}

		cs.setPossibleValues(this);

		CriteriumSet.CriteriaIterator ite = cs.criteriaIterator();



		LocalDataSeries[] dat = new LocalDataSeries[ite.getNbCombinations()];
		boolean[] datUsed = new boolean[ite.getNbCombinations()];

		for (int i = 0 ; i < dat.length ; i++) {
			dat[i] = new LocalDataSeries(null);
		}
		int used = 0;

		//	ArrayList<InternalDataPoint> aList = new ArrayList<InternalDataPoint>(100);
		for (InternalExecution exec : getExecListCopy()) {
			Vector<InternalDataPoint> vec = exec.dataPoints;
			int size = vec.size();
			for (int i = 0 ; i < size ; i++) {
				InternalDataPoint dp = vec.get(i);
				if (dp.isDefinedForProperty(methodName) == false) {
					continue;
				}

				if (!noXAxis && dp.isDefinedForProperty(xAxis) == false) {
					continue;
				}
				if (filters != null) {
					if (filter(dp, filters) == false) {
						continue;
					}
				}

				int index = 0;
				ite = cs.criteriaIterator();
				for (List<PairList<String, String>> col : ite) {
					//	dat[index] = new LocalDataSeries(null);
					if (filterCrit(dp, col, filters)) {
						float y = Float.parseFloat(dp.getValue(methodName));
						String x;
						if (noXAxis)
							x = "CONSTANT";
						else
							x = dp.getValue(xAxis);
						dat[index].addPoint(x, y);
						if (datUsed[index] == false) {
							datUsed[index] = true;
							used++;
						}
					}
					dat[index].crit = col;
					index++;
				}
			}
		}

		List<DataSeries> toRet = new ArrayList<DataSeries>(used);

		for (int i = 0 ; i < dat.length ; i++) {
			if (datUsed[i] == true) {
				toRet.add(dat[i]);
			}
		}
		return toRet;
	}

	public void terminate() {
	}

	public static class LocalDataSeries extends DataSeries {

		private ArrayList<DataSeries.DataSeriesStruct> list = new ArrayList<DataSeries.DataSeriesStruct>();

		public LocalDataSeries(List<PairList<String,String>> crit) {
			super(crit);
		}

		@Override
		public Iterator<Float> getYsImpl() {
			ArrayList<Float> f = new ArrayList<Float>(list.size());
			for (DataSeriesStruct s : this) {
				f.add(s.yVal);
			}
			return f.iterator();
		}

		@Override
		public int getNumberOfPoints() {
			return list.size();
		}

		public Iterator<DataSeries.DataSeriesStruct> iterator() {
			Collections.sort(list);
			return list.iterator();
		}

		public void addPoint(String x, float y) {
			DataSeriesStruct s = new DataSeriesStruct();
			s.xVal = x;
			s.yVal = y;
			list.add(s);
		}
	}

	public int getNumberOfStoredDataPoints() {
		int i = 0;
		synchronized (execList) {
			for (InternalExecution exec : execList) {
				i += exec.dataPoints.size();
			}
		}
		return i;
	}

	@SuppressWarnings("unused")
	private class AnalysisBean {
		public float meanPropertyFrequency() {
			if (SmartDataPointCollector.usingDpHasRef == false) {
				return Float.NaN;
			}
			float i = getNumberOfStoredDataPoints();
			float tot = 0;
			int index = 0;
			for (InternalProperty prop : propList.values()) {
				tot += prop.dpHavingThisProp.size()/i;
				index++;
			}
			return tot/index;
		}

		public float meanStringValueSize() {
			int index = 0;
			int total = 0;
			for (InternalProperty prop : propList.values()) {
				for (String s : prop.getStringValues()) {
					index++;
					total += s.length();
				}
			}
			return (float)total/(float)index;
		}

		public float meanNumberOfStringValuePerProperty() {
			int index = 0;
			int total = 0;
			for (InternalProperty prop : propList.values()) {
				index++;
				total += prop.getStringValues().size();
			}
			return (float)total/(float)index;
		}

		public float meanNumberOfFloatValuePerProperty() {
			int index = 0;
			int total = 0;
			for (InternalProperty prop : propList.values()) {
				index++;
				total += prop.getNumberOfFloatValues();
			}
			return (float)total/(float)index;
		}

		public float meanPropertiesPerDataPoint() {
			int index = 0;
			int total = 0;
			for (InternalExecution exec : execList) {
				for (InternalDataPoint dp : exec.dataPoints) {
					index++;
					total += dp.valuesFloat.size() + dp.valuesString.size();
				}
			}
			return (float)total/(float)index;
		}

		public float meanDataPointPerExecution() {
			int index = 0;
			int total = 0;
			for (InternalExecution exec : execList) {
				index++;
				total += exec.dataPoints.size();
			}
			return (float)total/(float)index;
		}

		public float meanPropertyNameLength() {
			int index = 0;
			int total = 0;
			for (InternalProperty prop : propList.values()) {
				index++;
				total += prop.name.length();
			}
			return (float)total/(float)index;
		}

		public float meanPropertyUnitLength() {
			int index = 0;
			int total = 0;
			for (InternalProperty prop : propList.values()) {
				index++;
				total += prop.unit.length();
			}
			return (float)total/(float)index;
		}

		public DataSize getEstimatedMeanDataPointBytes() {
			DataSize s = new DataSize((long)(20f // internaldatapoint struct
					+ 40f // hashmap float struct
					+ 40f // hashmap string struct
					+ 8f + (4f * 2f * meanPropertiesPerDataPoint()) // hashmap array size
					+ (24f * meanPropertiesPerDataPoint())), DataSize.BYTE);
			s.setUnit(DataSize.KBYTE);
			return s;
		}

		public DataSize getEstimatedMeanExecutionBytes() {
			DataSize s = new DataSize((long)(16f // struct
					+ 16f // metadata struct
					+ 20f // data point arraylist struct
					+ 8f + (4f * meanDataPointPerExecution()) // arrayList internal array
					+ (getEstimatedMeanDataPointBytes().getBytes() * meanDataPointPerExecution())), DataSize.BYTE);
			s.setUnit(DataSize.KBYTE);
			return s;
		}

		public DataSize getEstimatedExecutionPartBytes() {
			DataSize s = new DataSize((long)(20f // execList struct
					+ 8f + (4f * execList.size()) // execList internal array
					+ execList.size() * getEstimatedMeanExecutionBytes().getBytes()), DataSize.BYTE);
			s.setUnit(DataSize.MBYTE);
			return s;
		}

		public DataSize getEstimatedAdditionalLinkBytes() {
			if (usingDpHasRef == false) {
				return null;
			}
			float tot = 12f + 44f + (29f * meanPropertyFrequency() * getNumberOfStoredDataPoints()); // dpHasProperty
			return new DataSize((long)(tot * propList.size()), DataSize.BYTE);
		}


		public DataSize getEstimatedMeanPropertyBytes() {
			DataSize s = new DataSize((long)(29f // struct
					+ 24f + 8f + meanPropertyNameLength()*2 // name
					+ 24f + 8f + meanPropertyUnitLength()*2 // unit
					+ 44f + (29f + 24f + 8f + meanStringValueSize()*2)*meanNumberOfStringValuePerProperty()
					+ 44f + (29f + 12f)*meanNumberOfFloatValuePerProperty()), DataSize.BYTE);
			s.setUnit(DataSize.KBYTE);
			return s;
		}

		public DataSize getEstimatedPropertyPartBytes() {
			DataSize s =  new DataSize((long)(44f // propList struct
					+ (29f * propList.size())
					+ propList.size() * getEstimatedMeanPropertyBytes().getBytes()), DataSize.BYTE);
			s.setUnit(DataSize.MBYTE);
			return s;					
		}

		public DataSize getEstimatedTotalBytes() {
			float f = getEstimatedExecutionPartBytes().getBytes() + getEstimatedPropertyPartBytes().getBytes();
			DataSize s;
			if (usingDpHasRef) {
				s = new DataSize((long)(f + getEstimatedAdditionalLinkBytes().getBytes()), DataSize.BYTE);
			} else {
				s = new DataSize((long)(f), DataSize.BYTE);
			}
			s.setUnit(DataSize.MBYTE);
			return s;			
		}

		public int getTreeMapInstances() {
			return 1 + propList.size() * 3;
		}

		public float getFloatInstances() {
			return (propList.size() * meanNumberOfFloatValuePerProperty());
		}

		public float getTreeMapEntryInstances() {
			float tot = 0;
			if (usingDpHasRef) {
				tot += propList.size() * ((meanPropertyFrequency()*getNumberOfStoredDataPoints()));
			}

			return tot + propList.size() * (1f + meanNumberOfStringValuePerProperty() +
					meanNumberOfFloatValuePerProperty());
		}

		public float getHashMapInstances() {
			return execList.size() * meanDataPointPerExecution() * 2f;
		}

		public float getHashMapEntryInstances() {
			return execList.size() * meanDataPointPerExecution() * (meanPropertiesPerDataPoint());
		}

		public int getNumberOfDataPoints() {
			return getNumberOfStoredDataPoints();
		}

		public int getNumberOfExecutions() {
			return execList.size();
		}

		public int getNumberOfProperties() {
			return propList.size();
		}

		public int getNumberOfPropertyStringValues() {
			int total = 0;
			for (InternalProperty prop : propList.values()) {
				total += prop.getStringValues().size();
			}
			return total;
		}

		public int getNumberOfPropertyFloatValues() {
			int total = 0;
			for (InternalProperty prop : propList.values()) {
				total += prop.getNumberOfFloatValues();
			}
			return total;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			try {
				Class<?> clazz = this.getClass();
				for (Method m : clazz.getMethods()) {
					if (m.getName().startsWith("mean")) {
						sb.append(String.format("%1$-40s %2$20s", m.getName(), m.invoke(this)));
						sb.append("\r\n");
					}
				}
				sb.append("\r\n");
				for (Method m : clazz.getMethods()) {
					if (m.getName().endsWith("Bytes")) {
						sb.append(String.format("%1$-40s %2$20s", m.getName(), m.invoke(this)));
						sb.append("\r\n");
					}
				}
				sb.append("\r\n");
				for (Method m : clazz.getMethods()) {
					if (m.getName().endsWith("Instances")) {
						sb.append(String.format("%1$-40s %2$20s", m.getName(), m.invoke(this)));
						sb.append("\r\n");
					}
				}
				sb.append("\r\n");
				for (Method m : clazz.getMethods()) {
					if (m.getName().startsWith("getNumber")) {
						sb.append(String.format("%1$-40s %2$20s", m.getName(), m.invoke(this)));
						sb.append("\r\n");
					}
				}
			}
			catch (Exception e) {
				throw new IllegalStateException(e);
			}
			return sb.toString();
		}

	}

	@Override
	public String toString() {
		return new AnalysisBean().toString();
	}

}

class InternalExecution implements java.io.Serializable {

	private static final long serialVersionUID = 1;

	Vector<InternalDataPoint> dataPoints;
	public InternalExecution(SmartDataPointCollector db, Execution e) {
		
		List<DataPoint> dpList = e.getDataPoints();
		
		int size = dpList.size();

		dataPoints = new Vector<InternalDataPoint>(size, 1);
		
		for (int i = 0 ; i < size ; i++) {
			dataPoints.add(new InternalDataPoint(db, dpList.get(i)));
		}
		
		Map<String, InternalProperty> copy = db.getPropListCopy();
		for (int i = 0 ; i < size ; i++) {
	//	for (InternalDataPoint idp : dataPoints) {
			InternalDataPoint idp = dataPoints.get(i);
			for (String ip : idp.valuesString.keySet()) {
				copy.remove(ip);
			}
			for (String ip : idp.valuesFloat.keySet()) {
				copy.remove(ip);
			}				
			for (InternalProperty s : copy.values()) {
				s.getStringValue(SmartDataPointCollector.WC);
			}			
		}
	}

	@Override
	public String toString() {
		return "exec with " + dataPoints.size() + "dps (use toLongString())";
	}

	public String toLongString() {
		return "Exec : " + dataPoints;
	}
}

class InternalDataPoint implements Comparable<InternalDataPoint>, java.io.Serializable {
	private static final long serialVersionUID = 1;

//	public static final Object sem = new Object();

	int id;

	private static int gloCounter = 0;

	HashMap<String, String> valuesString = new HashMap<String, String>();
	HashMap<String, Float> valuesFloat = new HashMap<String, Float>();

	public int compareTo(InternalDataPoint dp) {
		return this.id - dp.id;
	}

	public InternalDataPoint(SmartDataPointCollector db, DataPoint dp) {
		super();
	//	synchronized (sem) {
			this.id = gloCounter++;
	//	}
		ArrayList<InternalProperty> interprop = new ArrayList<InternalProperty>(dp.getProperties().size());
		for (Property p : dp.getProperties()) {
			String value = p.getValue();
			if (value == null) {
				value = SmartDataPointCollector.NULL;
			}
			InternalProperty ip = db.getInternalProperty(p.getName());
			if (ip == null) {
				ip = new InternalProperty(p);
				db.addInternalProperty(ip.name, ip);
				boolean add = false;
				for (InternalExecution e : db.getExecListCopy()) {
					for (InternalDataPoint dp__ : e.dataPoints) {
						if (dp__.isDefinedForProperty(ip.name) == false) {
							add = true;
							break;
						}
					}
					if (add) break;
				}
				if (add) {
					ip.getStringValue(SmartDataPointCollector.WC);
				}
			}
			if (SmartDataPointCollector.usingDpHasRef) {
				ip.addDataPointRef(this);
			}

			if (!p.isString()) {
				Float stock = ip.getFloatValue(p.getFloatValue());
				valuesString.remove(ip.name);
				valuesFloat.put(ip.name, stock);

			} else {
				String ipv = ip.getStringValue(value);
				valuesFloat.remove(ip.name);
				valuesString.put(ip.name, ipv);
			}
	//		copy.remove(ip.name);
			interprop.add(ip);
		}
	/*	for (InternalProperty s : copy.values()) {
			s.getStringValue(SebNativeDB.WC);
		}*/

		for (int i = 0 ; i < interprop.size() ; i++) {
			for (int j = 0 ; j < interprop.size() ; j++) {
				if (i!= j) {
					interprop.get(i).setRelatedTo(interprop.get(j));
				}
			}
		}
	}

	public String getValue(String key) {
		String s = valuesString.get(key);
		if (s != null) {
			return s;
		}
		Float f = valuesFloat.get(key);
		if (f != null) {
			return f.toString();
		}
		return null;
	}

	public boolean isDefinedForProperty(String s) {
		return (valuesFloat.get(s) != null) || (valuesString.get(s) != null);
	}

	@Override
	public String toString() {
		return "DP:"+valuesString + "," + valuesFloat;
	}
}

class InternalProperty implements java.io.Serializable {

	private static final long serialVersionUID = 1;

	private TreeMap<String,String> valuesString = new TreeMap<String,String>();
	private TreeMap<Float, Float> valuesFloat = new TreeMap<Float, Float>();
	TreeSet<InternalDataPoint> dpHavingThisProp;

	// test for avoiding null entries
	HashMap<String, InternalProperty> relatedProperties = new HashMap<String, InternalProperty>();

	byte flags = 0;

	public InternalProperty(Property p) {
		if (p instanceof ResultProperty) {
			flags |= 1;
		}
		if (SmartDataPointCollector.usingDpHasRef) {
			this.dpHavingThisProp = new TreeSet<InternalDataPoint>();
		}
		this.name = p.getName();
		this.unit = p.getUnit();
	}

	public void addDataPointRef(InternalDataPoint dp) {
		dpHavingThisProp.add(dp);
	}

	public void setRelatedTo(InternalProperty p) {
		relatedProperties.put(p.name, p);
	}

	public boolean isNumbersOnly() {
		return !((flags & 2) > 0);
	}

	public boolean isResult() {
		return ((flags & 1) > 0);
	}

	
	// Remove synchronization here (sep 2015): general sync is made on exeList
	public Set<String> getValues() {
		SimpleSet<String> s = new SimpleSet<String>(valuesFloat.size() + valuesString.size());
	//	synchronized (valuesFloat) {
			for (Float f : valuesFloat.values()) {
				s.add(f.toString());
			}
	//	}
		s.addAll(valuesString.values());
		return s;
	}

	public Float getFloatValue(Float val) {
	//	synchronized (valuesFloat) {
			Float ipv = valuesFloat.get(val);
			if (ipv == null) {
				ipv = val;
				valuesFloat.put(val,val);
			}
			return ipv;
	//	}
	}

	public String getStringValue(String value) {
		if (value != SmartDataPointCollector.WC) {
			flags |= 2;
		}
	//	synchronized (valuesString) {
			String ipv = valuesString.get(value);
			if (ipv == null) {
				ipv = value;
				valuesString.put(value, value);
			}
			return ipv;			
	//	}
	}

	Set<String> getStringValues() {
	//	synchronized (valuesString) {
			return valuesString.keySet();
	//	}
	}

	int getNumberOfFloatValues() {
		return valuesFloat.size();
	}

	String name;
	String unit;

	@Override
	public String toString() {
		return name + "("+unit+")";
	}
}
