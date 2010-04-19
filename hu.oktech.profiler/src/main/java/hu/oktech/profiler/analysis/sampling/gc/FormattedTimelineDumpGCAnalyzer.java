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
import hu.oktech.profiler.util.DateTimeUtils;

import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;

/**
 * GC Dump that is somewhat more informative than the plain dump
 * 
 * @author Istvan Soos
 *
 */
public class FormattedTimelineDumpGCAnalyzer extends SimpleTimelineDumpGCAnalyzer {

	@Override
	public void print(Writer out) throws IOException {
		if (list.isEmpty())
			return;

		GCData prev = list.get(0);
		long first = prev.getSystemTime();

		DecimalFormat decimalFormat = new DecimalFormat("#0.0000");

		for (GCData data : list) {
			long elapsed = data.getSystemTime() - first;

			long diff = data.getSystemTime() - prev.getSystemTime();

			double countIncrement = data.getCount() - prev.getCount();
			double timeIncrement = data.getTime() - prev.getTime();
			if (diff > 0) {
				countIncrement = countIncrement * 1000000.0 / diff;
				timeIncrement = timeIncrement * 1000000.0 / diff;
			}

			out.write(
			        DateTimeUtils.formatElapsedTime(elapsed) + "\t"
			        + decimalFormat.format(countIncrement) + "\t"
			        + decimalFormat.format(timeIncrement) + "\n");

			prev = data;
		}
	}

}
