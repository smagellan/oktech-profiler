/* 
 * Copyright (c) 2009-2010, OKTECH-Info Kft. - All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Please check attribution requirements at
 * 
 * http://code.google.com/p/oktech-profiler/wiki/License
 * 
 */
package hu.oktech.profiler.runtime.timers;

/**
 * The abstract timer utility class. It is the base class of recurring
 * operations that can be started or stopped.
 * 
 * @author Istvan Soos
 * 
 */
public abstract class AbstractTimer implements Runnable {

	protected long samplingTimeMillis = 1000; // ms
	protected int samplingTimeNanos = 0; // ns

	protected Thread thread;
	protected volatile boolean running;

	public AbstractTimer(long samplingTimeMillis, int samplingTimeNanos) {
		super();
		this.samplingTimeMillis = samplingTimeMillis;
		this.samplingTimeNanos = samplingTimeNanos;
	}

	public AbstractTimer() {
		super();
	}

	public abstract void init(String config);

	protected void doSleep(long time) {
		try {
			if (time <= 0) {
				Thread.sleep(samplingTimeMillis, samplingTimeNanos);
			} else {
				long total = samplingTimeMillis * 1000000 + samplingTimeNanos - time;
				if (total < 0)
					return;
				long millis = total / 1000000;
				int nanos = (int) (total - (millis * 1000000));
				Thread.sleep(millis, nanos);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			running = false;
			return;
		}
	}

	public abstract void doTimedTask();

	public abstract String execute(String command);

	public void run() {
		running = true;
		long time = -1;
		while (running) {
			// sleep
			doSleep(time);

			// do sampling
			try {
				long start = System.nanoTime();
				doTimedTask();
				time = System.nanoTime() - start;
			} catch (RuntimeException ex) {
				running = false;
				throw ex;
			}
		}
	}

	public void startThread(boolean daemon) {
		if (thread == null) {
			thread = new Thread(this, getClass().getName());
			thread.setDaemon(daemon);
			thread.start();
		}
	}

	public void stopThread() {
		running = false;
		if (thread != null) {
			try {
				if (thread != null)
					thread.join();
			} catch (InterruptedException e) {
			} finally {
				thread = null;
			}
		}
	}

	public boolean isRunning() {
		return running;
	}

	public int getStartOrder() {
		return 0;
	}

	public int getStopOrder() {
		return 0;
	}

}
