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
package hu.oktech.profiler.runtime.jmx;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

/**
 * The default {@link JmxConnectionService} implementation.
 * 
 * @author Istvan Soos
 */
public class JmxConnectionServiceImpl implements JmxConnectionService, JmxConnectionServiceImplMBean {

	protected ConcurrentMap<String, MBeanServerConnection> connectionMap =
	        new ConcurrentHashMap<String, MBeanServerConnection>();

	public JmxConnectionServiceImpl() {
		connectionMap.put("local", ManagementFactory.getPlatformMBeanServer());
	}

	public MBeanServerConnection get(String name) {
		return connectionMap.get(name);
	}

	public void register(String name, String url, String username, String password) {
		if ("local".equals(name))
			throw new RuntimeException("'local' is a reserved name for connections!");

		try {
			JMXServiceURL jmxurl = new JMXServiceURL(url);
			Hashtable<String, Object> context = new Hashtable<String, Object>();
			JMXConnector e = JMXConnectorFactory.connect(jmxurl, context);
			connectionMap.put(name, e.getMBeanServerConnection());
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public MBeanServer getLocal() {
		return (MBeanServer) get("local");
	}

}
