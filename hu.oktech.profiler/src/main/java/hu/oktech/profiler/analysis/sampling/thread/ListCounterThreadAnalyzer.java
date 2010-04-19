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

import hu.oktech.profiler.runtime.sampling.thread.ThreadData;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A simple analyzer that counts the items in the stack traces
 * 
 * @author Istvan Soos
 */
public class ListCounterThreadAnalyzer extends AbstractThreadAnalyzer {

	protected Map<String, Long> itemCounts = new HashMap<String, Long>();

	public void add(ThreadData td) {
		if (td.getStackTrace() != null) {
			for (StackTraceElement ste : td.getStackTrace()) {
				String key = ste.getClassName() + "." + ste.getMethodName();

				Long cnt = itemCounts.get(key);
				if (cnt == null) {
					itemCounts.put(key, 1L);
				} else {
					itemCounts.put(key, cnt + 1);
				}
			}
		}
	}

	public void sort() {
		List<ListItem> list = new LinkedList<ListItem>();
		for (Entry<String, Long> e : itemCounts.entrySet())
			list.add(new ListItem(e.getKey(), e.getValue()));
		Collections.sort(list);

		Map<String, Long> m = new LinkedHashMap<String, Long>();
		for (ListItem li : list)
			m.put(li.name, li.count);
		this.itemCounts = m;
	}

	protected class ListItem implements Comparable<ListItem> {
		String name;
		long count;

		public ListItem(String name, long count) {
			super();
			this.name = name;
			this.count = count;
		}

		public int compareTo(ListItem o) {
			if (this.count < o.count)
				return 1;
			if (this.count > o.count)
				return -1;
			return 0;
		}

	}

	public void print(int minCounter, Writer out) throws IOException {
		for (Entry<String, Long> e : itemCounts.entrySet()) {
			out.write(e.getValue().toString());
			out.write("\t");
			out.write(e.getKey());
			out.write("\n");
		}
	}

    public void postProcess() {
    	sort();
    }
}
