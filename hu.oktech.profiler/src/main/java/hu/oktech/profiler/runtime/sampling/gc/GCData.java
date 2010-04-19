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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

/**
 * Garbage collection data from sampling
 * 
 * @author Istvan Soos
 * 
 */
public class GCData implements Serializable, Externalizable {

	private static final long serialVersionUID = 1L;

	protected String name;
	protected long count;
	protected long time;
	protected long systemTime;

	public GCData() {
		super();
	}

	public GCData(String name, long count, long time, long systemTime) {
		super();
		this.name = name;
		this.count = count;
		this.time = time;
		this.systemTime = systemTime;
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		in.readByte(); // version id -- only 1 is known, not checked
		this.name = in.readUTF();
		this.count = in.readLong();
		this.time = in.readLong();
		this.systemTime = in.readLong();
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeByte(1); // version id
		out.writeUTF(name);
		out.writeLong(count);
		out.writeLong(time);
		out.writeLong(systemTime);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getSystemTime() {
		return systemTime;
	}

	public void setSystemTime(long systemTime) {
		this.systemTime = systemTime;
	}

}
