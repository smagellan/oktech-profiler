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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

/**
 * Memory data from sampling
 * 
 * @author Istvan Soos
 * 
 */
public class MemoryData implements Serializable, Externalizable {

	private static final long serialVersionUID = 1L;

	protected long usedHeap;
	protected long usedNonHeap;
	protected int pendingFinalizationCount;
	protected long systemTime;

	public MemoryData() {
		super();
	}

	public MemoryData(long usedHeap, long usedNonHeap, int pendingFinalizationCount, long systemTime) {
		super();
		this.usedHeap = usedHeap;
		this.usedNonHeap = usedNonHeap;
		this.pendingFinalizationCount = pendingFinalizationCount;
		this.systemTime = systemTime;
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		in.readByte(); // version id -- only 1 is known, not checked
		this.usedHeap = in.readLong();
		this.usedNonHeap = in.readLong();
		this.pendingFinalizationCount = in.readInt();
		this.systemTime = in.readLong();
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeByte(1); // version id
		out.writeLong(usedHeap);
		out.writeLong(usedNonHeap);
		out.writeInt(pendingFinalizationCount);
		out.writeLong(systemTime);
	}

	public long getUsedHeap() {
		return usedHeap;
	}

	public void setUsedHeap(long usedHeap) {
		this.usedHeap = usedHeap;
	}

	public long getUsedNonHeap() {
		return usedNonHeap;
	}

	public void setUsedNonHeap(long usedNonHeap) {
		this.usedNonHeap = usedNonHeap;
	}

	public int getPendingFinalizationCount() {
		return pendingFinalizationCount;
	}

	public void setPendingFinalizationCount(int pendingFinalizationCount) {
		this.pendingFinalizationCount = pendingFinalizationCount;
	}

	public long getSystemTime() {
		return systemTime;
	}

	public void setSystemTime(long systemTime) {
		this.systemTime = systemTime;
	}

}
