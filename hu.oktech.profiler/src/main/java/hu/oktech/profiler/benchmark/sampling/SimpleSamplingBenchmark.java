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
package hu.oktech.profiler.benchmark.sampling;

import hu.oktech.profiler.runtime.Profiler;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class SimpleSamplingBenchmark {

	public long measure(int threads) throws InterruptedException, ExecutionException {
		long st = System.currentTimeMillis();

		ExecutorService tp = Executors.newFixedThreadPool(threads);
		List<FutureTask<Integer>> list = new LinkedList<FutureTask<Integer>>();

		for (int x = 0; x < threads; x++) {
			FutureTask<Integer> task = new FutureTask<Integer>(new Callable<Integer>() {

				public Integer call() throws Exception {
					long sum = 0;
					for (int i = 0; i < 1000000000; i++) {
						sum += i * i;
						sum -= i;
						sum += i * i;
						sum -= i;
						sum += i * i;
						sum -= i;
						sum += i * i;
						sum -= i;
					}
					return (int) (sum % 10);
				}
			});
			list.add(task);
			tp.execute(task);
		}
		for (FutureTask<Integer> task : list)
			task.get();
		tp.shutdown();

		return System.currentTimeMillis() - st;
	}

	public static void main(String[] args) throws Exception {
		{
			long time = Long.MAX_VALUE;
			for (int i = 0; i < 10; i++)
				time = Math.min(time, new SimpleSamplingBenchmark().measure(10));
			System.out.println(time);
		}

		Profiler.main(new String[] { "sampling=1" });
		{
			long time = Long.MAX_VALUE;
			for (int i = 0; i < 10; i++)
				time = Math.min(time, new SimpleSamplingBenchmark().measure(10));
			System.out.println(time);
		}

	}
}
