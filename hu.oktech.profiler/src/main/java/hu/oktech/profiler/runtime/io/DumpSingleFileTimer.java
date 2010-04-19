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
package hu.oktech.profiler.runtime.io;

import hu.oktech.profiler.runtime.queues.QueueUtils;
import hu.oktech.profiler.runtime.timers.AbstractTimer;
import hu.oktech.profiler.util.PropertyUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Queue;

/**
 * A timer that drains a queue and writes it to a file in a binary format.
 * 
 * @author Istvan Soos
 */
public class DumpSingleFileTimer extends AbstractTimer {

	protected Queue<Object> queue;
	protected File outputFile;

	public DumpSingleFileTimer(long samplingTimeMillis, int samplingTimeNanos, Queue<Object> queue,
	        File outputFile) {
		super(samplingTimeMillis, samplingTimeNanos);
		this.queue = queue;
		this.outputFile = outputFile;
	}

	public DumpSingleFileTimer(String config) {
		super();
		init(config);
	}

	public void init(String config) {
		Properties props = PropertyUtils.parse(config, "\\;");

		this.samplingTimeMillis = Integer.parseInt(props.getProperty("millis", "5000"));
		this.samplingTimeNanos = Integer.parseInt(props.getProperty("nanos", "0"));

		String queueName = props.getProperty("queue", "default");
		this.queue = QueueUtils.get().get(queueName);

		if (props.getProperty("file") != null) {
			outputFile = new File(props.getProperty("file"));
			if (outputFile.getParentFile() != null)
				outputFile.getParentFile().mkdirs();
		} else {
			String dir = props.getProperty("dir", "tmp/profiler");
			SimpleDateFormat sdf =
			        new SimpleDateFormat(props.getProperty("dateformat", "yyyy-MM-dd-HH-mm-ss"));
			outputFile = new File(dir + "/" + sdf.format(new Date()));
			if (outputFile.getParentFile() != null)
				outputFile.getParentFile().mkdirs();
		}
	}

	public synchronized void doTimedTask() {
		if (queue.isEmpty())
			return;

		try {
			BufferedOutputStream bos =
			        new BufferedOutputStream(new FileOutputStream(outputFile, true), 655360);
			ObjectOutputStream oos = new ObjectOutputStream(bos);

			while (!queue.isEmpty()) {
				oos.writeBoolean(true);
				oos.writeObject(queue.poll());
			}
			oos.writeBoolean(false);
			oos.flush();
			oos.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public String execute(String command) {
		if ("flush".equals(command)) {
			doTimedTask();
			return "flushed";
		}
		return null;
	}

	@Override
	public void stopThread() {
		doTimedTask();
		super.stopThread();
	}

	public int getStartOrder() {
		return 0;
	}

	public int getStopOrder() {
		return Integer.MAX_VALUE;
	}

}
