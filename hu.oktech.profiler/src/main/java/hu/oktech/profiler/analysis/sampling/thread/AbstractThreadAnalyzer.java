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
package hu.oktech.profiler.analysis.sampling.thread;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Queue;

import hu.oktech.profiler.runtime.io.DumpFileReader;
import hu.oktech.profiler.runtime.queues.QueueUtils;
import hu.oktech.profiler.runtime.sampling.thread.ThreadData;
import hu.oktech.profiler.util.processing.CallbackController;
import hu.oktech.profiler.util.processing.ObjectCallback;

/**
 * Common methods for thread analyzers
 * 
 * @author Istvan Soos
 */
public abstract class AbstractThreadAnalyzer {

	public abstract void add(ThreadData td);

	public abstract void print(final int minCounter, Writer out) throws IOException;
	
	public abstract void postProcess();

	public void drainQueue(String name) {
		Queue<Object> q = QueueUtils.get().get(name);
		while (!q.isEmpty()) {
			Object o = q.poll();
			if (o instanceof ThreadData)
				add((ThreadData) o);
		}
	}

	public void readFile(File file) {
		DumpFileReader dfr = new DumpFileReader(file);
		dfr.read(new ObjectCallback() {

			public void process(Object object, CallbackController controller) {
				if (object instanceof ThreadData)
					add((ThreadData) object);
			}
		});
	}

	public void print() {
		print(0);
	}

	public void print(final int minCounter) {
		try {
			print(minCounter, new OutputStreamWriter(System.out));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
