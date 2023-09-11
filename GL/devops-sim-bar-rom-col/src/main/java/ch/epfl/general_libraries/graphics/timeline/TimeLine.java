package ch.epfl.general_libraries.graphics.timeline;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.util.ArrayList;
import ch.epfl.general_libraries.graphics.timeline.TimeLineSet.ReceComm;
import ch.epfl.general_libraries.graphics.timeline.TimeLineSet.SendComm;
import ch.epfl.general_libraries.utils.MoreCollections;

public class TimeLine implements Comparable<TimeLine> {
	
	private static Color muchBrightherRed = new Color(160, 160, 160);
	private static Color deepRed = new Color(251, 18, 0);
//	private static Color dswpMsgBlue = new Color(56, 137, 204);
	private static Color purple = new Color(117, 115, 186);
	
	public static interface Type {
		public Paint getPaint(int s1, int s2);
	}
	
	public static class CustomType implements Type {
		final Color c;
		
		public CustomType(Color c) {
			this.c = c;
		}
		public Paint getPaint(int s1, int s2) {
			return c;
		}				
	}
	
	public static enum EnumType implements Type {
		
		COMPUTE ( Color.GREEN, Color.YELLOW),
		WAIT (Color.WHITE, Color.GRAY),
		TIME_OUT_WAIT (Color.WHITE),
		IDLE (Color.WHITE, Color.GRAY.brighter()), 
		ERROR (muchBrightherRed, Color.WHITE), 
		OK (Color.GREEN), 
		INIT (deepRed), 
		AGGREGATE (purple);
		
		final Color c1;
		final Color c2;
		
		private EnumType(Color c1, Color c2) {
			this.c1 = c1;
			this.c2 = c2;
		}
		
		private EnumType(Color c) {
			this.c1 = c;
			this.c2 = c;
		}
		
		public Paint getPaint(int s1, int s2) {
			if (c1 != c2) {
				return new GradientPaint(0, s1, c1, 0, s2, c2);
			} else {
				return c1;
			}
		}
	}
	
	public static class Job {
		Color color;
		double start;
		double end;
		String desc;
		Type type;
		int pass;
	}
	
	Color specialColor = null;
	
	private int index = -1;
	int displayedIndex = -1;
	
	TimeLineSet associatedSet;
	
	ArrayList<Job> jobPhases = new ArrayList<Job>();
	ArrayList<Double> reads = new ArrayList<Double>();
	
	ArrayList<TimeLineSet.SendComm> sends = new ArrayList<TimeLineSet.SendComm>();
	ArrayList<TimeLineSet.ReceComm> receives = new ArrayList<TimeLineSet.ReceComm>();
	
	String timeLineGroup;
	String title;

	public TimeLine() {
		// TODO Auto-generated constructor stub
	}
	
	public TimeLine(int index, String title) {
		this.timeLineGroup = "default";
		this.title = title;
		this.index = index;
	}
	
	public TimeLine(int index, String string, String string2) {
		this.timeLineGroup = string;
		this.title = string2;
		this.index = index;
	}

	public TimeLine(int index, String string, String string2, Color color) {
		this(index, string, string2);
		specialColor = color;
	}
	
	public void setGroup(String g) {
		this.timeLineGroup = g;
	}
	
	public void setTitle(String g) {
		this.title = g;
	}

	public void addRead(double d) {
		reads.add(d);
	}
	
	public void addJobPhase(double start, double end, String desc) {
		addJobPhase(start, end, desc, null);
	}
	
	public void addJobPhase(double start, double end, String desc, Color c, int pass) {
		Job j = new Job();
		j.start = start;
		j.end = end;
		j.desc = desc;
		j.color = c;
		j.pass = pass;
		jobPhases.add(j);		
	}	
	
	public void addJobPhase(double start, double end, String desc, Color c) {
		this.addJobPhase(start, end, desc, c, 0);		
	}
	
	public void addJobPhase(double start, double end, Type t, String desc) {
		Job j = new Job();
		j.start = start;
		j.end = end;
		j.desc = desc;
		j.type = t;
		jobPhases.add(j);
	}
	
	private void unmatch() {
		if (associatedSet != null) {
			associatedSet.unmatch();
		}
	}
	
	public double getLastEventTime() {
		double max = 0;
		max = MoreCollections.maxDouble(reads);
		for (Job j : jobPhases) {
			if (j.end > max)
				max = j.end;
		}
		for (ReceComm rece : receives) {
			if (rece.time > max)
				max = rece.time;
		}
		for (SendComm send : sends) {
			if (send.time > max)
				max = send.time;
		}
		return max;
	}
	
	public void addSend(double time, int index, double size) {
		sends.add(new SendComm(time, index, size, null, this));
		unmatch();
	}
	
	public void addSend(double time, int index, double size, String desc) {
		sends.add(new SendComm(time, index, size, desc, this));
		unmatch();
	}
	
	public void addReceive(double time, int index) {		
		receives.add(new ReceComm(time, index, this));
		unmatch();		
	}
	
	public ArrayList<Job> getJobPhases() {
		return jobPhases;
	}	
	
	public ArrayList<ReceComm> getReceiveEvents() {
		return receives;
	}
	
	public ArrayList<SendComm> getSendEvents() {
		return sends;
	}
	
	public ArrayList<Double> getReads() {
		return reads;
	}

	@Override
	public int compareTo(TimeLine o) {
		if (timeLineGroup != null && o.timeLineGroup != null) {
			int c = timeLineGroup.compareTo(o.timeLineGroup);
			if (c != 0) return c;
			c = title.compareTo(o.title);
		}
		return index - o.index;
	}

}
