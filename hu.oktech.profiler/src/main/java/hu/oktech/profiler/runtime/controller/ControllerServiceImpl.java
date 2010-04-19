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
package hu.oktech.profiler.runtime.controller;

import hu.oktech.profiler.runtime.controller.sampling.GCSamplingController;
import hu.oktech.profiler.runtime.controller.sampling.MemorySamplingController;
import hu.oktech.profiler.runtime.controller.sampling.ThreadSamplingController;

import java.lang.instrument.Instrumentation;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 
 * 
 * @author Istvan Soos
 */
public class ControllerServiceImpl implements ControllerService, ControllerServiceImplMBean {

	protected Instrumentation instrumentation;
	protected ConcurrentMap<String, Controller> controllers = new ConcurrentHashMap<String, Controller>();

	public void setInstrumentation(Instrumentation instrumentation) {
		this.instrumentation = instrumentation;
	}

	public void register(String name, String type, String config) {
		Controller controller = null;
		if ("sampling/thread".equals(type) || "sampling-thread".equals(type)) {
			controller = new ThreadSamplingController();
		} else if ("sampling/memory".equals(type) || "sampling-memory".equals(type)) {
			controller = new MemorySamplingController();
		} else if ("sampling/gc".equals(type) || "sampling-gc".equals(type)) {
			controller = new GCSamplingController();
		} else {
			try {
				controller = (Controller) Class.forName(type).newInstance();
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		controller.init(config, instrumentation);
		controllers.put(name, controller);
	}

	public String execute(String name, String command) {
		Controller c = controllers.get(name);
		if (c == null)
			throw new RuntimeException("No controller: " + name);
		return c.execute(command);
	}

	public Controller get(String name) {
		return controllers.get(name);
	}

	public boolean isRunning(String name) {
		Controller c = controllers.get(name);
		if (c == null)
			throw new RuntimeException("No controller: " + name);
		return c.isRunning();
	}

	public void remove(String name) {
		controllers.remove(name);
	}

	public void start(String name) {
		Controller c = controllers.get(name);
		if (c == null)
			throw new RuntimeException("No controller: " + name);
		c.start();
	}

	public void stop(String name) {
		Controller c = controllers.get(name);
		if (c == null)
			throw new RuntimeException("No controller: " + name);
		c.stop();
	}

}
