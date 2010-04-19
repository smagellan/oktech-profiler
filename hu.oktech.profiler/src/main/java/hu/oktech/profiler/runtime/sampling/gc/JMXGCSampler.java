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

import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

/**
 * JMX-based GC sampler
 * 
 * @author Istvan Soos
 * 
 */
public class JMXGCSampler implements GCSampler {

	protected GarbageCollector[] gcs;

	public JMXGCSampler(MBeanServerConnection mBeanServerConnection) {
		try {
			Set<ObjectName> names = mBeanServerConnection.queryNames(
			        new ObjectName(ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE+",*"), null);

			List<GarbageCollector> list = new ArrayList<GarbageCollector>();

			for (ObjectName name : names) {
				GarbageCollectorMXBean mxbean = ManagementFactory.newPlatformMXBeanProxy(
				        mBeanServerConnection, name.getCanonicalName(), GarbageCollectorMXBean.class);
				String gcname = mxbean.getName();
				list.add(new GarbageCollector(gcname, mxbean));
			}

			this.gcs = list.toArray(new GarbageCollector[list.size()]);			
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (MalformedObjectNameException e) {
			throw new RuntimeException(e);
		}
	}

	public GCData[] getGCData() {
		GCData[] result = new GCData[gcs.length];
		for (int i = 0; i < result.length; i++) {
			GarbageCollector gc = gcs[i];
			result[i] = new GCData(gc.name, gc.mxbean.getCollectionCount(),
			        gc.mxbean.getCollectionTime(), System.currentTimeMillis());
		}
		return result;
	}

	public static class GarbageCollector {
		protected String name;
		protected GarbageCollectorMXBean mxbean;

		public GarbageCollector(String name, GarbageCollectorMXBean mxbean) {
			super();
			this.name = name;
			this.mxbean = mxbean;
		}

	}
}
