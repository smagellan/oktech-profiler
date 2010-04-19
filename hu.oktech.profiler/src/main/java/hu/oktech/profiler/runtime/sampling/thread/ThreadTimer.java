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
package hu.oktech.profiler.runtime.sampling.thread;

import hu.oktech.profiler.runtime.jmx.JmxConnectionUtils;
import hu.oktech.profiler.runtime.queues.QueueUtils;
import hu.oktech.profiler.runtime.timers.AbstractTimer;
import hu.oktech.profiler.util.PropertyUtils;

import java.util.Properties;
import java.util.Queue;

/**
 * Thread time that samples the running threads with the provided sampler.
 * 
 * @author Istvan Soos
 * 
 */
public class ThreadTimer extends AbstractTimer {

	protected Queue<Object> queue;
	protected ThreadSampler sampler;
	protected long[] threadIds;
	protected int maxDepth;

	protected int systemTimerFreq = 1;
	protected int cpuTimerFreq = 0;

	public ThreadTimer(long samplingTimeMillis, int samplingTimeNanos, Queue<Object> queue,
	        ThreadSampler sampler, long[] threadIds, int maxDepth, int systemTimerFreq, int cpuTimerFreq) {
		super(samplingTimeMillis, samplingTimeNanos);
		this.queue = queue;
		this.sampler = sampler;
		this.threadIds = threadIds;
		this.maxDepth = maxDepth;
		this.systemTimerFreq = systemTimerFreq;
		this.cpuTimerFreq = cpuTimerFreq;
	}

	public ThreadTimer(String config) {
		super();
		init(config);
	}

	public void init(String config) {
		Properties props = PropertyUtils.parse(config, "\\;");

		this.samplingTimeMillis = Integer.parseInt(props.getProperty("millis", "1000"));
		this.samplingTimeNanos = Integer.parseInt(props.getProperty("nanos", "0"));

		String queueName = props.getProperty("queue", "default");
		this.queue = QueueUtils.get().get(queueName);

		String samplerName = props.getProperty("sampler", "jmx/local");
		if ("local".equals(samplerName)) {
			this.sampler = new LocalThreadSampler();
		} else if (samplerName.startsWith("jmx/") || samplerName.startsWith("jmx-")) {
			this.sampler = new JMXThreadSampler(JmxConnectionUtils.get().get(samplerName.substring(4)));
		} else {
			throw new RuntimeException("Unknown sampler: " + samplerName);
		}

		String threadIdsProp =
		        props.getProperty("threadIds", props.getProperty("threadids", props.getProperty("threadid")));
		if (threadIdsProp != null) {
			String[] p = threadIdsProp.split(",");
			if (p.length > 0) {
				this.threadIds = new long[p.length];
				for (int i = 0; i < p.length; i++)
					this.threadIds[i] = Long.parseLong(p[i]);
			}
		}

		this.maxDepth = Integer.MAX_VALUE;
		String maxDepthProp = props.getProperty("maxDepth");
		if (maxDepthProp != null)
			this.maxDepth = Integer.parseInt(maxDepthProp);

		this.systemTimerFreq = Integer.parseInt(props.getProperty("systemTimerFreq", "1"));
		this.cpuTimerFreq = Integer.parseInt(props.getProperty("cpuTimerFreq", "0"));
	}

	protected int systemTimerCount = 0;
	protected int cpuTimerCount = 0;

	public synchronized void doTimedTask() {
		int timer = 0;
		if (systemTimerFreq > 0) {
			if (systemTimerCount == 0) {
				systemTimerCount = systemTimerFreq;
				timer = timer | ThreadSampler.TIMER_SYSTEM;
			}
			systemTimerCount--;
		}
		if (cpuTimerFreq > 0) {
			if (cpuTimerCount == 0) {
				cpuTimerCount = cpuTimerFreq;
				timer = timer | ThreadSampler.TIMER_THREAD;
			}
			cpuTimerCount--;
		}

		if (threadIds == null) {
			for (ThreadData td : sampler.getAllThreadData(maxDepth, timer)) {
				if (td != null)
					queue.add(td);
			}
		} else {
			for (ThreadData td : sampler.getThreadData(threadIds, maxDepth, timer)) {
				if (td != null)
					queue.add(td);
			}
		}
	}

	public String execute(String command) {
		return null;
	}

}
