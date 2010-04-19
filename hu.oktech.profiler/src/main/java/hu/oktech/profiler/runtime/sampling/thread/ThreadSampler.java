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

/**
 * Generic thread sampler interface
 * 
 * @author Istvan Soos
 */
public interface ThreadSampler {

	public static final int TIMER_NONE = 0;
	public static final int TIMER_SYSTEM = 1;
	public static final int TIMER_THREAD = 2;
	public static final int TIMER_ALL = 3;

	public long[] getAllThreadIds();

	public ThreadData getThreadData(long threadId, int maxDepth, int timer);

	public ThreadData[] getThreadData(long[] threadIds, int maxDepth, int timer);

	public ThreadData[] getAllThreadData(int maxDepth, int timer);

	public long getThreadCpuTime(long threadId);

}
