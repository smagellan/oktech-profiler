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
package hu.oktech.profiler.runtime.sampling.memory;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

import javax.management.MBeanServerConnection;

/**
 * JMX-based memory sampler
 * 
 * @author Istvan Soos
 * 
 */
public class JMXMemorySampler implements MemorySampler {

	protected MemoryMXBean memoryMXBean;

	public JMXMemorySampler(MemoryMXBean memoryMXBean) {
		super();
		this.memoryMXBean = memoryMXBean;
	}

	public JMXMemorySampler(MBeanServerConnection mBeanServerConnection) {
		try {
			this.memoryMXBean = ManagementFactory.newPlatformMXBeanProxy(
			        mBeanServerConnection, ManagementFactory.MEMORY_MXBEAN_NAME,
			        MemoryMXBean.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public MemoryData getMemoryData() {
		return new MemoryData(memoryMXBean.getHeapMemoryUsage().getUsed(),
		        memoryMXBean.getNonHeapMemoryUsage().getUsed(),
		        memoryMXBean.getObjectPendingFinalizationCount(),
		        System.currentTimeMillis());
	}

}
