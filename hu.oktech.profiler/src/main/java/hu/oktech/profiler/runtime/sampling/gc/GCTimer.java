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
package hu.oktech.profiler.runtime.sampling.gc;

import java.util.Properties;
import java.util.Queue;

import hu.oktech.profiler.runtime.jmx.JmxConnectionUtils;
import hu.oktech.profiler.runtime.queues.QueueUtils;
import hu.oktech.profiler.runtime.timers.AbstractTimer;
import hu.oktech.profiler.util.PropertyUtils;

/**
 * Timer for GC stat sampling
 * 
 * @author Istvan Soos
 */
public class GCTimer extends AbstractTimer {

	protected Queue<Object> queue;
	protected GCSampler sampler;

	public GCTimer(String config) {
		super();
		init(config);
	}

	public void init(String config) {
		Properties props = PropertyUtils.parse(config, "\\;");

		this.samplingTimeMillis = Integer.parseInt(props.getProperty("millis", "20000"));
		this.samplingTimeNanos = Integer.parseInt(props.getProperty("nanos", "0"));

		String queueName = props.getProperty("queue", "default");
		this.queue = QueueUtils.get().get(queueName);

		String samplerName = props.getProperty("sampler", "jmx/local");
		if (samplerName.startsWith("jmx/") || samplerName.startsWith("jmx-")) {
			sampler = new JMXGCSampler(JmxConnectionUtils.get().get(samplerName.substring(4)));
		} else {
			throw new RuntimeException("Unknown sampler: " + samplerName);
		}
	}

	public void doTimedTask() {
		for (GCData gcdata : sampler.getGCData())
			queue.add(gcdata);
	}

	public String execute(String command) {
		return null;
	}

}
