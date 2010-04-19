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
package hu.oktech.profiler.analysis.sampling.gc;

import hu.oktech.profiler.runtime.sampling.gc.GCData;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Plain GC dump
 * 
 * @author Istvan Soos
 */
public class SimpleTimelineDumpGCAnalyzer extends AbstractGCAnalyzer {

	protected List<GCData> list = new LinkedList<GCData>();

	public void add(GCData data) {
		list.add(data);
	}

	public void sort() {
		Collections.sort(list, new Comparator<GCData>() {

			public int compare(GCData o1, GCData o2) {
				if (o1.getSystemTime() < o2.getSystemTime())
					return -1;
				if (o1.getSystemTime() > o2.getSystemTime())
					return 1;
				return 0;
			}
		});
	}

	public void print(Writer out) throws IOException {
		for (GCData data : list)
			out.write(data.getSystemTime() + "\t" + data.getCount() + "\t" + data.getTime() + "\n");
	}

	public void postProcess() {
		sort();
	}

}
