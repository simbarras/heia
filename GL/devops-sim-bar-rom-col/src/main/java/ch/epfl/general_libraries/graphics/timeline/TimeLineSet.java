package ch.epfl.general_libraries.graphics.timeline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import ch.epfl.general_libraries.simulation.Time;


public class TimeLineSet extends ArrayList<TimeLine> {
	
	private static final long serialVersionUID = 1L;
	public ArrayList<Communication> communications = null;
	private boolean matched = false;
	private Time timeUnit = Time.NANOSECOND;
	
	public static class SendComm implements Comparable<SendComm> {
		int index;
		double size;
		double time;
		String desc;
		TimeLine tl;
		
		public SendComm(double time, int index, double size, String desc, TimeLine tl) {
			this.index = index;
			this.time = time;
			this.size = size;
			this.desc = desc;
			this.tl = tl;
		}

		@Override
		public int compareTo(SendComm o) {
			return this.index - o.index;
		}
	}
	
	public static class ReceComm implements Comparable<ReceComm> {
		int index;
		double time;
		TimeLine tl;
		public ReceComm(double time, int index, TimeLine tl) {
			this.time = time;
			this.index = index;
			this.tl = tl;
		}
		@Override
		public int compareTo(ReceComm o) {
			return this.index - o.index;
		}
	}
	
	public static class Communication {
		public Communication(SendComm send, ReceComm rece) {
			this.startTime = send.time;
			this.endTime = rece.time;
			this.start = send.tl;
			this.end = rece.tl;
			this.reference = send.index;
			this.size = send.size +"";
			this.desc = send.desc;
		}
		
		public Communication(double startTime, double endTime, TimeLine start, TimeLine end, int ref, String size, String desc) {
			this.startTime = startTime;
			this.endTime = endTime;
			this.start = start;
			this.end = end;
			this.reference = ref;
			this.size = size;
			this.desc = desc;
		}
		
		double startTime;
		double endTime;
		TimeLine start;
		TimeLine end;
		int reference;
		String size;
		String desc;		
	}

	public TimeLineSet() {}
	
	public void unmatch() {
		matched = false;
	}
	
	public Time getTimeUnit() {
		return timeUnit;
	}
	
	public boolean isMatched() {
		return matched;
	}
	
	@Override
	public boolean add(TimeLine l) {
		if (l == null) return false;
		matched = false;
		l.associatedSet = this;
		return super.add(l);
	}
	
	@Override
	public void add(int index, TimeLine t) {
		t.associatedSet = this;		
		super.add(index, t);
		matched = false;
	}
	
	public void addAll(TimeLine[] t) {
		for (TimeLine tl : t) {
			this.add(tl);
		}
		matched = false;
	}
	
	@Override
	public boolean addAll(Collection<? extends TimeLine> c) {
		for (TimeLine t : c) {
			t.associatedSet = this;
		}
		matched = false;
		return super.addAll(c);
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends TimeLine> c) {	
		for (TimeLine t : c) {
			t.associatedSet = this;
		}		
		matched = false;
		return super.addAll(index, c);
	}

	@Override
	public TimeLine set(int index, TimeLine t) {
		t.associatedSet = this;
		matched = false;
		TimeLine old = super.set(index, t);
		old.associatedSet = null;
		return old;
	}
	
	@Override
	public boolean remove(Object o) {
		matched = false;
		boolean b = super.remove(o);
		if (b) {
			((TimeLine)o).associatedSet = null;
		}
		return b;
	}
	
	@Override
	public TimeLine remove(int i) {
		matched = false;
		TimeLine old = super.remove(i);
		old.associatedSet = null;
		return old;
	}
	
	@Override
	public void removeRange(int i, int j) {
		throw new IllegalStateException("Not implemented");
	}
	
	public ArrayList<Communication> getCommunications() {
		return communications;
	}
	
	private boolean matching = false;
	
	public ArrayList<Communication> matchCommunications() {
		synchronized(this) {
			matching = true;
			this.notifyAll();
		}
		if (matched == true) return communications;
		communications = new ArrayList<Communication>();
			
		int totSend = 0;
		int totRece = 0;
		for (TimeLine i : this) {
			totSend += i.sends.size();
			totRece += i.receives.size();
		}
		ArrayList<SendComm> allsend = new ArrayList<SendComm>(totSend);
		ArrayList<ReceComm> allrece = new ArrayList<ReceComm>(totRece);

		for (TimeLine i : this) {
			allsend.addAll(i.sends);
			allrece.addAll(i.receives);
		}

		Collections.sort(allsend);
		Collections.sort(allrece);

		if (!(allsend.size() == 0 || allrece.size() == 0)) {
			int indexSend = 0;
			int indexRece = 0;
			while (indexSend != allsend.size() && indexRece != allrece.size()) {
				SendComm currentSend = allsend.get(indexSend);
				ReceComm currentRece = allrece.get(indexRece);
				if (currentSend.index < currentRece.index) {
					System.out.println("Message " + currentSend.index + " sent only...");
					indexSend++;
					currentSend = allsend.get(indexSend);
	
				} else if ( currentSend.index > currentRece.index ){
					System.out.println("Message " + currentRece.index + " received only...");
					indexRece++;					
					currentRece = allrece.get(indexRece);
				}
				if (currentSend.index == currentRece.index) {
					communications.add(new Communication(currentSend, currentRece));
					indexSend++;
					indexRece++;
				}
			}
				
				
			/*	for (TimeLine i : this) {
					for (Map.Entry<Integer, Object[]> send : i.getSendEvents().entrySet()) {
						boolean found = false;
						for (TimeLine alt : this) {
							if (alt != i) {
								for (Map.Entry<Integer, Double> receive : alt.getReceiveEvents().entrySet()) {
									if (send.getKey().equals(receive.getKey())) {
										Object[] obj = send.getValue();
										Communication com = new Communication((Double)obj[0], receive.getValue(), i, alt, send.getKey(), (Double)obj[1]+"", (String)obj[2]);
										communications.add(com);
										found = true;
										break;
									}
								}
							}
							if (found) break;
						}
						if (found == false) {
							System.out.println("COmmunication " + send.getKey() + " not matched (from " + i.title + ")");
						//	throw new IllegalStateException();
						}
					}
				}*/
		}
		Collections.sort(this);
		synchronized(this) {
			matched = true;
			matching = false;
		}
		return communications;
	}

	public double getLastEventTime() {
		double max = 0;
		for (TimeLine l : this) {
			if (l.getLastEventTime() > max)
				max = l.getLastEventTime();
		}
		return max;
	}

	public boolean isMatching() {
		return matching;
	}

	public void setMatching(boolean b) {
		matching = true;
		
	}

	public void matchCommunicationsAsync() {
		synchronized(this) {
			if (isMatched() == false && isMatching() == false) {
				matching = true;
				new Thread() {
					public void run() {
						matchCommunications();
					}
				}.start();
				try {
					this.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	

}
