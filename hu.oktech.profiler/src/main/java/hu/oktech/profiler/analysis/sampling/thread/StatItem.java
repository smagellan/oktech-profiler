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
package hu.oktech.profiler.analysis.sampling.thread;

import java.io.Serializable;

/**
 * Stat item
 * 
 * @author Istvan Soos
 * 
 */
public class StatItem implements Serializable {

    private static final long serialVersionUID = 1L;

    protected long count = 0;
    protected long minSystemTime = Long.MAX_VALUE;
    protected long maxSystemTime = 0;

    public String toString() {
        return count + " (" + minSystemTime + " " + ((maxSystemTime - minSystemTime)) + ")";
    }

    public void incrementCount() {
        count++;
    }

    public void markSystemTime(long time) {
        if (time > 0) {
            time = time / 1000000000;
            minSystemTime = Math.min(time, minSystemTime);
            maxSystemTime = Math.max(time, maxSystemTime);
        }
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getMinSystemTime() {
        return minSystemTime;
    }

    public void setMinSystemTime(long minTime) {
        this.minSystemTime = minTime;
    }

    public long getMaxSystemTime() {
        return maxSystemTime;
    }

    public void setMaxSystemTime(long maxTime) {
        this.maxSystemTime = maxTime;
    }

}
