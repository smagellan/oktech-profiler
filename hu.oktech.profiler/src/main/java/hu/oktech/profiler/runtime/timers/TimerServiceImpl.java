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

import hu.oktech.profiler.runtime.io.DumpSingleFileTimer;
import hu.oktech.profiler.runtime.sampling.gc.GCTimer;
import hu.oktech.profiler.runtime.sampling.memory.MemoryTimer;
import hu.oktech.profiler.runtime.sampling.thread.ThreadTimer;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The default {@link TimerService} implementation.
 * 
 * @author Istvan Soos
 * 
 */
public class TimerServiceImpl implements TimerService, TimerServiceImplMBean {

	protected ConcurrentMap<String, AbstractTimer> timers = new ConcurrentHashMap<String, AbstractTimer>();

	public synchronized void stopAll() {
		List<AbstractTimer> list = new LinkedList<AbstractTimer>(timers.values());
		Collections.sort(list, new Comparator<AbstractTimer>() {

			public int compare(AbstractTimer o1, AbstractTimer o2) {
				int x = o1.getStopOrder();
				int y = o2.getStopOrder();
				if (x < y)
					return -1;
				if (x > y)
					return 1;
				return 0;
			}

		});
		for (AbstractTimer at : list)
			at.stopThread();
	}

	public synchronized void removeAllNonRunning() {
		Set<String> remove = new HashSet<String>();
		for (Entry<String, AbstractTimer> e : timers.entrySet()) {
			if (!e.getValue().isRunning())
				remove.add(e.getKey());
		}
		for (String key : remove)
			remove(key);
	}

	public synchronized boolean isRunning(String name) {
		AbstractTimer at = timers.get(name);
		if (at == null)
			return false;
		return at.isRunning();
	}

	public synchronized void register(String name, String type, String config) {
		AbstractTimer timer = null;
		if ("sampling/thread".equals(type) || "sampling-thread".equals(type)) {
			timer = new ThreadTimer(config);
		} else if ("sampling/memory".equals(type) || "sampling-memory".equals(type)) {
			timer = new MemoryTimer(config);
		} else if ("sampling/gc".equals(type) || "sampling-gc".equals(type)) {
			timer = new GCTimer(config);
		} else if ("dump/single".equals(type) || "dump-single".equals(type)) {
			timer = new DumpSingleFileTimer(config);
		} else if ("cascaded".equals(type)) {
			timer = new CascadedTimer(config);
		} else {
			try {
				timer = (AbstractTimer) Class.forName(type).newInstance();
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
			timer.init(config);
		}

		timers.put(name, timer);
	}

	public synchronized boolean exists(String name) {
		return timers.containsKey(name);
	}

	public synchronized void start(String name, boolean daemon) {
		AbstractTimer at = timers.get(name);
		if (at == null)
			throw new RuntimeException("No timer: " + name);
		at.startThread(daemon);
	}

	public synchronized void stop(String name) {
		AbstractTimer at = timers.get(name);
		if (at == null)
			throw new RuntimeException("No timer: " + name);
		at.stopThread();
	}

	public synchronized void remove(String name) {
		timers.remove(name);
	}

	public synchronized AbstractTimer get(String name) {
		return timers.get(name);
	}

	public synchronized String execute(String name, String command) {
		AbstractTimer at = timers.get(name);
		if (at == null)
			throw new RuntimeException("No timer: " + name);
		return at.execute(command);
	}

}
