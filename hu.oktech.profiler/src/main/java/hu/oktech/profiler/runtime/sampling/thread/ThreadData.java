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
package hu.oktech.profiler.runtime.sampling.thread;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.lang.management.LockInfo;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;

/**
 * Thread data acquired through thread sampling.
 * 
 * @author Istvan Soos
 */
public class ThreadData implements Serializable, Externalizable {

	private static final long serialVersionUID = 1L;

	protected String threadName;
	protected long threadId;
	protected long blockedTime;
	protected long blockedCount;
	protected long waitedTime;
	protected long waitedCount;
	protected LockInfo lock;
	protected String lockName;
	protected long lockOwnerId;
	protected String lockOwnerName;
	protected boolean inNative;
	protected boolean suspended;
	protected Thread.State threadState;
	protected StackTraceElement[] stackTrace;
	protected MonitorInfo[] lockedMonitors;
	protected LockInfo[] lockedSynchronizers;
	protected int maxDepth;
	protected long systemTime;
	protected long cpuTime;

	public ThreadData() {
		super();
	}

	public ThreadData(ThreadInfo info, int maxDepth, long systemTime, long cpuTime) {
		this.threadName = info.getThreadName();
		this.threadId = info.getThreadId();
		this.blockedTime = info.getBlockedTime();
		this.blockedCount = info.getBlockedCount();
		this.waitedTime = info.getWaitedTime();
		this.waitedCount = info.getWaitedCount();
		this.lock = info.getLockInfo();
		this.lockName = info.getLockName();
		this.lockOwnerId = info.getLockOwnerId();
		this.lockOwnerName = info.getLockOwnerName();
		this.inNative = info.isInNative();
		this.suspended = info.isSuspended();
		this.threadState = info.getThreadState();
		this.stackTrace = info.getStackTrace();
		this.lockedMonitors = info.getLockedMonitors();
		this.lockedSynchronizers = info.getLockedSynchronizers();
		this.maxDepth = maxDepth;
		this.systemTime = systemTime;
		this.cpuTime = cpuTime;
	}

	public ThreadData(Thread thread, StackTraceElement[] stackTrace, int maxDepth, long systemTime,
	        long cpuTime) {
		this.threadId = thread.getId();
		this.threadName = thread.getName();
		this.threadState = thread.getState();
		this.stackTrace = stackTrace;
		this.maxDepth = maxDepth;
		this.systemTime = systemTime;
		this.cpuTime = cpuTime;
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		readData(in);
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		writeData(out);
	}

	public void readData(DataInput in) throws IOException {
		in.readByte(); // version id -- only 1 is known, not checked
		threadName = readNullString(in);
		threadId = in.readLong();
		blockedTime = in.readLong();
		blockedCount = in.readLong();
		waitedTime = in.readLong();
		waitedCount = in.readLong();
		if (in.readBoolean())
			lock = new LockInfo(in.readUTF(), in.readInt());
		lockName = readNullString(in);
		lockOwnerId = in.readLong();
		lockOwnerName = readNullString(in);
		inNative = in.readBoolean();
		suspended = in.readBoolean();
		if (in.readBoolean())
			threadState = Thread.State.valueOf(in.readUTF());

		int stlength = in.readInt();
		if (stlength >= 0) {
			stackTrace = new StackTraceElement[stlength];
			for (int i = 0; i < stlength; i++)
				stackTrace[i] =
				        new StackTraceElement(readNullString(in), readNullString(in),
				        readNullString(in), in.readInt());
		}

		int lmlength = in.readInt();
		if (lmlength >= 0) {
			lockedMonitors = new MonitorInfo[lmlength];
			for (int i = 0; i < lmlength; i++) {
				StackTraceElement ste = null;
				if (in.readBoolean())
					ste = new StackTraceElement(readNullString(in), readNullString(in),
					        readNullString(in), in.readInt());
				lockedMonitors[i] = new MonitorInfo(readNullString(in), in.readInt(), in.readInt(), ste);
			}
		}

		int lslength = in.readInt();
		if (lslength >= 0) {
			lockedSynchronizers = new LockInfo[lslength];
			for (int i = 0; i < lslength; i++)
				lockedSynchronizers[i] = new LockInfo(readNullString(in), in.readInt());
		}
		maxDepth = in.readInt();
		systemTime = in.readLong();
		cpuTime = in.readLong();
	}

	public void writeData(DataOutput out) throws IOException {
		out.writeByte(1); // version id
		writeNullString(out, threadName);
		out.writeLong(threadId);
		out.writeLong(blockedTime);
		out.writeLong(blockedCount);
		out.writeLong(waitedTime);
		out.writeLong(waitedCount);
		if (lock != null) {
			out.writeBoolean(true);
			out.writeUTF(lock.getClassName());
			out.writeInt(lock.getIdentityHashCode());
		} else {
			out.writeBoolean(false);
		}
		writeNullString(out, lockName);
		out.writeLong(lockOwnerId);
		writeNullString(out, lockOwnerName);
		out.writeBoolean(inNative);
		out.writeBoolean(suspended);
		if (threadState == null) {
			out.writeBoolean(false);
		} else {
			out.writeBoolean(true);
			out.writeUTF(threadState.toString());
		}
		if (stackTrace == null) {
			out.writeInt(-1);
		} else {
			out.writeInt(stackTrace.length);
			for (StackTraceElement st : stackTrace) {
				writeNullString(out, st.getClassName());
				writeNullString(out, st.getMethodName());
				writeNullString(out, st.getFileName());
				out.writeInt(st.getLineNumber());
			}
		}
		if (lockedMonitors == null) {
			out.writeInt(-1);
		} else {
			out.writeInt(lockedMonitors.length);
			for (MonitorInfo lm : lockedMonitors) {
				StackTraceElement st = lm.getLockedStackFrame();
				if (st != null) {
					out.writeBoolean(true);
					writeNullString(out, st.getClassName());
					writeNullString(out, st.getMethodName());
					writeNullString(out, st.getFileName());
					out.writeInt(st.getLineNumber());
				} else {
					out.writeBoolean(false);
				}

				writeNullString(out, lm.getClassName());
				out.writeInt(lm.getIdentityHashCode());
				out.writeInt(lm.getLockedStackDepth());
			}
		}
		if (lockedSynchronizers == null) {
			out.writeInt(-1);
		} else {
			out.writeInt(lockedSynchronizers.length);
			for (LockInfo ls : lockedSynchronizers) {
				writeNullString(out, ls.getClassName());
				out.writeInt(ls.getIdentityHashCode());
			}
		}
		out.writeInt(maxDepth);
		out.writeLong(systemTime);
		out.writeLong(cpuTime);
	}

	protected String readNullString(DataInput in) throws IOException {
		if (in.readBoolean())
			return in.readUTF();
		return null;
	}

	protected void writeNullString(DataOutput out, String text) throws IOException {
		if (text == null) {
			out.writeBoolean(false);
		} else {
			out.writeBoolean(true);
			out.writeUTF(text);
		}
	}

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	public long getThreadId() {
		return threadId;
	}

	public void setThreadId(long threadId) {
		this.threadId = threadId;
	}

	public long getBlockedTime() {
		return blockedTime;
	}

	public void setBlockedTime(long blockedTime) {
		this.blockedTime = blockedTime;
	}

	public long getBlockedCount() {
		return blockedCount;
	}

	public void setBlockedCount(long blockedCount) {
		this.blockedCount = blockedCount;
	}

	public long getWaitedTime() {
		return waitedTime;
	}

	public void setWaitedTime(long waitedTime) {
		this.waitedTime = waitedTime;
	}

	public long getWaitedCount() {
		return waitedCount;
	}

	public void setWaitedCount(long waitedCount) {
		this.waitedCount = waitedCount;
	}

	public LockInfo getLock() {
		return lock;
	}

	public void setLock(LockInfo lock) {
		this.lock = lock;
	}

	public String getLockName() {
		return lockName;
	}

	public void setLockName(String lockName) {
		this.lockName = lockName;
	}

	public long getLockOwnerId() {
		return lockOwnerId;
	}

	public void setLockOwnerId(long lockOwnerId) {
		this.lockOwnerId = lockOwnerId;
	}

	public String getLockOwnerName() {
		return lockOwnerName;
	}

	public void setLockOwnerName(String lockOwnerName) {
		this.lockOwnerName = lockOwnerName;
	}

	public boolean isInNative() {
		return inNative;
	}

	public void setInNative(boolean inNative) {
		this.inNative = inNative;
	}

	public boolean isSuspended() {
		return suspended;
	}

	public void setSuspended(boolean suspended) {
		this.suspended = suspended;
	}

	public Thread.State getThreadState() {
		return threadState;
	}

	public void setThreadState(Thread.State threadState) {
		this.threadState = threadState;
	}

	public StackTraceElement[] getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(StackTraceElement[] stackTrace) {
		this.stackTrace = stackTrace;
	}

	public MonitorInfo[] getLockedMonitors() {
		return lockedMonitors;
	}

	public void setLockedMonitors(MonitorInfo[] lockedMonitors) {
		this.lockedMonitors = lockedMonitors;
	}

	public LockInfo[] getLockedSynchronizers() {
		return lockedSynchronizers;
	}

	public void setLockedSynchronizers(LockInfo[] lockedSynchronizers) {
		this.lockedSynchronizers = lockedSynchronizers;
	}

	public int getMaxDepth() {
		return maxDepth;
	}

	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}

	public long getSystemTime() {
		return systemTime;
	}

	public void setSystemTime(long systemTime) {
		this.systemTime = systemTime;
	}

	public long getCpuTime() {
		return cpuTime;
	}

	public void setCpuTime(long cpuTime) {
		this.cpuTime = cpuTime;
	}

}
