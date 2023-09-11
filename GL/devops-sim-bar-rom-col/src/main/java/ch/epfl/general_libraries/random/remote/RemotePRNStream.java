package ch.epfl.general_libraries.random.remote;

import java.io.IOException;

import ch.epfl.general_libraries.logging.Logger;
import ch.epfl.general_libraries.random.PRNStream;

public abstract class RemotePRNStream extends PRNStream {

	private static Logger logger = new Logger(RemotePRNStream.class);

	private int[][] state;
	private long[] cache;
	private Object bufferAccessMutex;
	private int readPointer;
	private int writePointer;
	public int shotSize;
	private float parallel_threshold;
	private int inv_check_rate;
	public boolean multiThread;
	public int cacheSize;

	Boolean parralelRunning = false;

	//	private int INV_CHECK_RATE = 100; // means that each 100 cycles cache is checked
	//	private float PARALLEL_THRESHOLD = 0.75f;
	private float THRESHOLD = 0.00f; // means that below 5%, cache will be filled again
	private float FILL_GOAL = 1; // means goal is to fill the buffer

	//	private int SHOTS_SIZE = 2000; // means ask numbers by pack of 32

	protected abstract void getNumbers(int numberToGet) throws IOException ;

	protected RemotePRNStream(int cacheSize, int shotSize, boolean multiThread, int seed) {
		super(seed);
		this.multiThread = multiThread;
		this.shotSize = Math.max(shotSize,1);
		this.parallel_threshold = Math.min((float)shotSize/(float)cacheSize,0.5f);
		this.inv_check_rate     = Math.max(1,Math.min(shotSize / 2, cacheSize / 2));
		this.cacheSize = cacheSize;
		bufferAccessMutex = new Object();
		cache = new long[cacheSize];
		readPointer = 0;
		writePointer = 0;
	}

	public void setShotsSize(int shot_size) {
		this.shotSize = shot_size;
	}

	protected void writeLong(long d) {
		synchronized(bufferAccessMutex) {
			cache[writePointer] = d;
			writePointer++;
			if (writePointer != readPointer) {
				if (writePointer >= cache.length) {
					writePointer = 0;
				}
			} else {
				throw new IllegalStateException("BUffer filled");
			}
			//	logger.trace("One long has been written : " + getBufferState());
		}
	}
	
	public double state() {
		throw new IllegalStateException("NOT IMPL");
	}	

	@Override
	public void setStateInt(int[][] state) {
		this.state = state;
	}

	@Override
	public int[][] getStateInt() {
		return state;
	}

	public String getBufferState() {
		if (readPointer < writePointer) {
			return "0----READ[" + readPointer + "]-----WRITE[" + writePointer + "]----" + (cache.length-1);
		} else if (writePointer < readPointer) {
			return "0----WRITE[" + writePointer + "]-----READ[" + readPointer + "]----" + (cache.length-1);
		} else {
			return "0----READ AND WRITE : " + readPointer;
		}
	}

	protected long readLong() {
		long toReturn = 0;
		if (readPointer % inv_check_rate == 0) { checkThreshold(); }
		boolean ask = true;
		while (ask) {
			synchronized(bufferAccessMutex) {
				ask =(readPointer == writePointer);
			}
			if (ask) {
				//	logger.trace("Read pointer met write pointer. Asking more");
				askMoreParallel();
			}
		}
		synchronized (bufferAccessMutex) {
			toReturn = cache[readPointer];
			readPointer++;
			if (readPointer >= cache.length) {
				readPointer = 0;
			}
			//	logger.trace("One long has been read : " + getBufferState());
		}
		return toReturn;
	}

	private int getCacheFreeSize() {
		int reste;
		synchronized (bufferAccessMutex) {
			// readPointer == writePointer mean buffer exhausted
			if (readPointer < writePointer) {
				reste = cache.length - writePointer + readPointer-1;
			} else if (readPointer > writePointer) {
				reste = readPointer - writePointer -1;
			} else {
				reste = cache.length-1;
			}
		}
		return reste;
	}

	protected void checkThreshold() {
		logFreeCapacity();
		float ratio =  1 -((float)getCacheFreeSize() / (float)cache.length);
		if (!multiThread) {
			askMoreParallel();
		} else {
			Reloader reloader = getReloaderInstance(this);
			if (ratio < THRESHOLD) {
				askMoreParallel();
			} else if (ratio < parallel_threshold) {
				synchronized (reloader) {
					if (reloader.isRunning() == false) {
						reloader.reload();
					}
				}
			}
		}
	}

	protected void askMoreParallel() {
		if (multiThread) {
			Reloader reloader = getReloaderInstance(this);
			synchronized (reloader) {
				if (reloader.isRunning() == false) {
					reloader.reload();
				} /*else {
					try {
						reloader.wait();
					}
					catch (InterruptedException e) {}
				}*/
			}
		} else {
			askMore();
		}
	}

	private Reloader singleton = null;

	public Reloader getReloaderInstance(RemotePRNStream s) {
		if (singleton == null) {
			singleton = new Reloader(s);
		}
		return singleton;
	}



	protected void askMore() {
		//	logger.debug("Before ask more :" + getBufferState());
		// different strategies :
		// 1. Fill the cache
		// 2. Reach a certain threshold

		// a. ask all in one
		// b. ask in multiple "shots"

		// 1. Determine number of shots
		//	logger.debug("Fill goal is " +(FILL_GOAL*100)+"%, cache size is " + cache.length);
		//	logFreeCapacity();
		float numberToOrder = ((cache.length * (FILL_GOAL-1)) + getCacheFreeSize());
		int passes = (int)Math.ceil(numberToOrder/ this.shotSize);
		//		logger.debug("Client will now order " + (int)numberToOrder + " numbers in " + passes + " passes");
		for (int i = 0 ; i < passes ; i++) {
			try {
				//				logger.trace("   Pass " + i + " : cache has " + getCacheFreeSize() + " free");
				//				logger.trace("   Asking for " + Math.min(this.shotSize, getCacheFreeSize()) + " numbers");
				getNumbers(Math.min(this.shotSize, getCacheFreeSize()));
			}
			catch (IOException e) {
				logger.error("RemotePRNStream experienced problem when asking PRNs remotely : ",e);
				throw new IllegalStateException("Impossible to get numbers from server",e);
			}
		}
		//	logger.debug("After ask more :" + getBufferState());
	}

	private void logFreeCapacity() {
		if (logger.getEffectiveLevel().equals(Logger.TRACE) || logger.getEffectiveLevel().equals(Logger.DEBUG)) {
			float fill_rate = (float)getCacheFreeSize()/(float)(cache.length-1);
			fill_rate = 100 - Math.round(fill_rate*100);
			logger.debug("Actually, cache is filled at " + fill_rate + " %");
		}
	}


	/**
	 * Returns one integer selected randomly from the range <code>[from, to]
	 * </code>.
	 */
	@Override
	public long nextUnsignedInt() {
		return readLong();
	}

	@Override
	public int nextInt() {
		return super.nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE, readLong());
	}

	@Override
	public int nextInt(int from, int to) {
		return super.nextInt(from, to, readLong());
	}

	@Override
	public void nextArrayOfInt(int from, int to, int[] dest, int start, int n) {
		throw new IllegalStateException("Not yet implemented");
	}

	@Override
	public double nextDouble() {
		throw new IllegalStateException("Not implmented");
		//	return //readLong();
	}

	@Override
	public double nextRandomImpl() {
		throw new IllegalStateException("Not implmented");
		//	return //readLong();
	}

	@Override
	public void nextArrayOfDouble(double[] dest, int start, int n) {
		throw new IllegalStateException("Not yet implemented");
	}


}


class Reloader implements Runnable {

	private static Logger logger = new Logger(Reloader.class);

	private RemotePRNStream s = null;
	private Boolean isRunning = false;
	private Thread t = null;


	Reloader(RemotePRNStream s) {
		this.s = s;
	}

	public boolean isRunning() {
		return isRunning;
	}


	public void run() {
		Thread.currentThread().setName("RemotePRNG Reloader");
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		synchronized (this) {
			isRunning = true;
		}
		try {
			s.askMore();
		}
		catch (Exception e) {
			logger.error("&(/)&/&/////////////////////////////////////////");
			System.exit(0);
		}
		synchronized (this) {
			isRunning = false;
			t = null;
			this.notifyAll();
		}
	}

	public void reload() {
		synchronized(this) {
			if (t != null) {
				try {
					this.wait();
				} catch (InterruptedException e) {}
			} else {
				t = new Thread(this);
				t.start();
			}
		}
	}
}