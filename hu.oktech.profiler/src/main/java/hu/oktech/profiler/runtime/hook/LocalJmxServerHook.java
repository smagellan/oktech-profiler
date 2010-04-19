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
package hu.oktech.profiler.runtime.hook;

import hu.oktech.profiler.runtime.Profiler;

import java.lang.management.ManagementFactory;
import java.util.Properties;

/**
 * A simple delay hook that tries to access the platform mbean server with a time interval. This
 * hook turned to be not reliable in all cases, please don't use it without testing.
 * 
 * @author Istvan Soos
 * 
 */
public class LocalJmxServerHook implements Runnable {

    private long hookRetryMillis = 15000;
    private Properties props;

    public LocalJmxServerHook(Properties props) {
        super();
        this.props = props;
        try {
            this.hookRetryMillis = Long.parseLong(props.getProperty("hook.millis", "15000"));
        } catch (Exception ex) {
        }
    }

    public void run() {
        for (;;) {
            try {
                Thread.sleep(hookRetryMillis);
            } catch (InterruptedException e) {
                return;
            }

            try {
                ManagementFactory.getPlatformMBeanServer();
                Profiler.init(props);
                // System.out.println("OKTECH-Profiler Agent started...");
                return;
            } catch (Throwable ex) {
            }
        }

    }
}