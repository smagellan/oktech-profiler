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


import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

import javax.management.MBeanServerConnection;

/**
 * The basic, JMX-based thread sampler implementation.
 * 
 * @author Istvan Soos
 */
public class JMXThreadSampler implements ThreadSampler {

	protected ThreadMXBean threadMXBean;

	public JMXThreadSampler(ThreadMXBean threadMXBean) {
		super();
		this.threadMXBean = threadMXBean;
	}

	public JMXThreadSampler(MBeanServerConnection connection) {
		super();
		try {
			this.threadMXBean = ManagementFactory.newPlatformMXBeanProxy(connection,
			        ManagementFactory.THREAD_MXBEAN_NAME, ThreadMXBean.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public long[] getAllThreadIds() {
		return threadMXBean.getAllThreadIds();
	}

	public ThreadData getThreadData(long threadId, int maxDepth, int timer) {
		ThreadInfo threadInfo = threadMXBean.getThreadInfo(threadId, maxDepth);
		if (threadInfo == null)
			return null;

		long systemTime =
		        (timer & ThreadSampler.TIMER_SYSTEM) == ThreadSampler.TIMER_SYSTEM ? System.nanoTime() : 0;
		long cpuTime =
		        (timer & ThreadSampler.TIMER_THREAD) == ThreadSampler.TIMER_THREAD
		        ? getThreadCpuTime(threadId) : 0;
		return new ThreadData(threadInfo, maxDepth, systemTime, cpuTime);
	}

	public ThreadData[] getThreadData(long[] threadIds, int maxDepth, int timer) {
		ThreadData[] result = new ThreadData[threadIds.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = getThreadData(threadIds[i], maxDepth, timer);
		}
		return result;
	}

	public ThreadData[] getAllThreadData(int maxDepth, int timer) {
		return getThreadData(getAllThreadIds(), maxDepth, timer);
	}

	public long getThreadCpuTime(long threadId) {
		return threadMXBean.getThreadCpuTime(threadId);
	}
}
