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
import hu.oktech.profiler.runtime.timers.TimerUtils;

import java.io.File;
import java.util.Properties;

import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;

/**
 * A simple hook that monitors the presence of a file. If the file becomes present it will init and
 * the specified controllers, if it disappears it will stop every timer and remove them.
 * 
 * @author Istvan Soos
 */
public class PollFileHook implements Runnable {

    protected Properties props;
    protected String pollFile;
    protected long hookRetryMillis = 15000;

    protected boolean running = false;

    public PollFileHook(String pollFile, Properties props) {
        super();
        this.pollFile = pollFile;
        this.props = props;
        try {
            this.hookRetryMillis = Long.parseLong(props.getProperty("hook.millis", "15000"));
        } catch (Exception ex) {
            // nothing
        }
    }

    public void run() {
        for (;;) {
            try {
                Thread.sleep(hookRetryMillis);
            } catch (InterruptedException e) {
                return;
            }

            if (running) {
                File file = new File(pollFile);
                if (!file.exists()) {
                    TimerUtils.get().stopAll();
                    TimerUtils.get().removeAllNonRunning();
                    running = false;
                }
            } else {
                // check poll file
                File file = new File(pollFile);
                if (file.exists()) {
                    try {
                        Profiler.init(props);
                        running = true;
                    } catch (MBeanRegistrationException e) {
                        e.printStackTrace();
                        return;
                    } catch (NotCompliantMBeanException e) {
                        e.printStackTrace();
                        return;
                    } catch (MalformedObjectNameException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }
        }
    }

}
