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
package hu.oktech.profiler.benchmark.clock;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

/**
 * A simple test utility to benchmark different clocks.
 * 
 * @author Istvan Soos
 * 
 */
public class StandardClockBenchmark {

	ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

	public static void main(String[] args) {
		new StandardClockBenchmark().benchmark();
	}

	public void doSystemMillis(int times) {
		while (times > 0) {
			times--;
			System.currentTimeMillis();
		}
	}

	public void doSystemNano(int times) {
		while (times > 0) {
			times--;
			System.nanoTime();
		}
	}

	public void doThreadCpu(int times) {
		while (times > 0) {
			times--;
			threadMXBean.getCurrentThreadCpuTime();
		}
	}

	public void benchmark() {
		System.out.println(measureInSystemNano(new Runnable() {

			public void run() {
				doSystemMillis(100000000);
			}
		}));
		System.out.println(measureInSystemNano(new Runnable() {

			public void run() {
				doSystemNano(100000000);
			}
		}));
		System.out.println(measureInSystemNano(new Runnable() {

			public void run() {
				doThreadCpu(100000000);
			}
		}));
	}

	public long measureInSystemNano(Runnable runnable) {
		long start = System.nanoTime();
		runnable.run();
		return System.nanoTime() - start;
	}
}
