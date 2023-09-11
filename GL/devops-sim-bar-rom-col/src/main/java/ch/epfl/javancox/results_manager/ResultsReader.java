package ch.epfl.javancox.results_manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import ch.epfl.general_libraries.utils.SimpleMap;


public class ResultsReader {

	private SmartDataPointCollector db;
	BufferedReader br;

	public ResultsReader(Reader r) {

		br = new BufferedReader(r);
		db = new SmartDataPointCollector();

	}

	public static class Transformer {
		public String transform(String s) {
			return s;
		}
	}

	public static class TimeTransformer extends Transformer {

		private SimpleDateFormat format = new SimpleDateFormat("h:mm:ss");

		@Override
		public String transform(String s) {
			try {
				s = s.replaceAll(" ","");
				if (s.length() < 6) {
					s = "0:" + s;
				}
				Date date = format.parse(s);
				java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
				cal.setTime(date);
				
				return cal.get(Calendar.SECOND)+(cal.get(Calendar.MINUTE)*60)+(cal.get(Calendar.HOUR)*3600)+"";
			} catch (Exception e) {
				System.out.println(s);
				return null;
			}
			//	return null;
		}
	}

	public void read(String separator) throws IOException {
		String line = br.readLine();
		String[] head = line.split(separator);
		Transformer[] t = new Transformer[head.length];
		boolean[] b = new boolean[head.length];
		for (int i = 0 ; i < head.length ; i++) {
			t[i] = new Transformer();
			b[i] = true;
		}
		read(head, t, b, separator);
	}

	public void read(String[] headers, Transformer[] trans, boolean[] types, String separator) throws IOException {
		if (headers.length != trans.length) {
			throw new IllegalArgumentException("Param array sizes mismatch");
		}
		if (headers.length != types.length) {
			throw new IllegalArgumentException("Param array sizes mismatch");
		}
		String line;
		int index = 0;
		while ((line = br.readLine()) != null) {
			if (line.startsWith("#")){
				continue;
			}
			Map<String, String> data = new SimpleMap<String, String>();
			Map<String, String> vals = new SimpleMap<String, String>();
			String[] sp = line.split(separator);
			for (int i = 0 ; i < Math.min(sp.length, headers.length) ; i++) {
				if (headers[i].equals("SKIP")) {
					continue;
				}
				if (sp[i].equals("")) {
					continue;
				}
				if (sp[i].equals(" ")) {
					continue;
				}
				if (types[i]) {
					data.put(headers[i], trans[i].transform(sp[i]));
				} else {
					vals.put(headers[i], trans[i].transform(sp[i]));
				}
			}
			data.put("line_number", ""+index++);
			
		// to change to the DataPoint concept
		//	db.addSimple(data, vals);
			System.out.println("Line " + index);
		}
	}

	public SmartDataPointCollector getDB() {
		return db;
	}


}
