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
package hu.oktech.profiler.runtime.queues;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * The default {@link QueueService} implementation that stores the queues in
 * memory and uses concurrent linked queues as internal implementation.
 * 
 * @author Istvan Soos
 * 
 */
public class QueueServiceImpl implements QueueService, QueueServiceImplMBean {

	protected ConcurrentMap<String, Queue<?>> queueMap = new ConcurrentHashMap<String, Queue<?>>();

	@SuppressWarnings("unchecked")
	public <T> Queue<T> get(String name, boolean create) {
		Queue<T> q = (Queue<T>) queueMap.get(name);
		if (q != null)
			return q;

		if (create) {
			queueMap.putIfAbsent(name, new ConcurrentLinkedQueue<T>());
			return (Queue<T>) queueMap.get(name);
		} else {
			return null;
		}
	}

	public void clear(String name) {
		Queue<Object> q = get(name, false);
		if (q != null)
			q.clear();
	}

	public int getSize(String name) {
		Queue<Object> q = get(name, false);
		if (q != null)
			return q.size();
		return 0;
	}

	public boolean isEmpty(String name) {
		Queue<Object> q = get(name, false);
		if (q != null)
			return q.isEmpty();
		return true;
	}

	public boolean isExists(String name) {
		return get(name, false) != null;
	}

	public void remove(String name) {
		queueMap.remove(name);
	}

	public <T> Queue<T> get(String name) {
		return get(name, true);
	}

}
