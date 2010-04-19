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


import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.Map;

/**
 * Thread sampler that gets the thread data directly, without the JMX MBeans in
 * some cases.
 * 
 * @author Istvan Soos
 */
public class LocalThreadSampler extends JMXThreadSampler {

	public LocalThreadSampler() {
		super(ManagementFactory.getThreadMXBean());
	}

	@Override
	public ThreadData[] getAllThreadData(int maxDepth, int timer) {
		Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();
		ThreadData[] result = new ThreadData[map.size()];

		int index = 0;
		for (Map.Entry<Thread, StackTraceElement[]> entry : map.entrySet()) {
			StackTraceElement[] st = entry.getValue();
			if (st.length > maxDepth)
				st = Arrays.copyOf(st, maxDepth);
			long systemTime =
			        (timer & ThreadSampler.TIMER_SYSTEM) == ThreadSampler.TIMER_SYSTEM ? System.nanoTime()
			        : 0;
			long cpuTime =
			        (timer & ThreadSampler.TIMER_THREAD) == ThreadSampler.TIMER_THREAD
			        ? getThreadCpuTime(entry.getKey().getId()) : 0;
			result[index] = new ThreadData(entry.getKey(), st, maxDepth, systemTime, cpuTime);
			index++;
		}
		return result;
	}

}
