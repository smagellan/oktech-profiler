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
package hu.oktech.profiler.util;

/**
 * Helper utilities for data and time operations
 * 
 * @author Istvan Soos
 * 
 */
public abstract class DateTimeUtils {

	public static String formatElapsedTime(long time) {
		long ms = time % 1000;
		time = time / 1000;
		long sec = time % 60;
		time = time / 60;
		long min = time % 60;
		time = time / 60;
		long hour = time % 24;
		time = time / 24;
		long day = time;

		StringBuilder sb = new StringBuilder();
		sb.append(day);
		sb.append("d ");
		if (hour < 10)
			sb.append("0");
		sb.append(hour);
		sb.append(":");
		if (min < 10)
			sb.append("0");
		sb.append(min);
		sb.append(":");
		if (sec < 10)
			sb.append("0");
		sb.append(sec);
		sb.append(".");
		if (ms < 100)
			sb.append("0");
		if (ms < 10)
			sb.append("0");
		sb.append(ms);
		return sb.toString();
	}
}
