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
package hu.oktech.profiler.runtime.timers;

import hu.oktech.profiler.util.PropertyUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * A timer that batches other times. This can be used to periodically
 * synchronize and serialize the execution of the provided timers.
 * 
 * @author Istvan Soos
 */
public class CascadedTimer extends AbstractTimer {

	protected AbstractTimer[] timers;
	protected AbstractTimer actualTimer;
	protected int nextTimer = 0;

	public CascadedTimer() {
		super();
	}

	public CascadedTimer(String config) {
		super();
		init(config);
	}

	public void doTimedTask() {
		actualTimer.doTimedTask();
		nextTimer();
	}

	public void init(String config) {
		List<AbstractTimer> tlist = new ArrayList<AbstractTimer>();

		Properties props = PropertyUtils.parse(config, "\\;");
		String[] tss = props.getProperty("timers").split(",");
		for (String ts : tss) {
			String[] p = ts.split("\\:", 2);
			String name = p[0];
			int x = 1;
			if (p.length == 2)
				x = Integer.parseInt(p[1]);
			for (int i = 0; i < x; i++)
				tlist.add(TimerUtils.get().get(name));
		}
		timers = tlist.toArray(new AbstractTimer[tlist.size()]);

		nextTimer();
	}

	public void nextTimer() {
		this.actualTimer = timers[nextTimer];
		this.samplingTimeMillis = actualTimer.samplingTimeMillis;
		this.samplingTimeNanos = actualTimer.samplingTimeNanos;
		nextTimer++;
		if (nextTimer >= timers.length)
			nextTimer = 0;
	}

	public String execute(String command) {
		String[] parts = command.split(";", 2);
		try {
			int index = Integer.parseInt(parts[0]);
			return timers[index].execute(parts[1]);
		} catch (Exception ex) {
			return "Exception: " + ex.getMessage();
		}
	}
}
