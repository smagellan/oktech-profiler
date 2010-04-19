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
package hu.oktech.profiler.runtime.controller.sampling;

import hu.oktech.profiler.runtime.controller.AbstractTimerController;
import hu.oktech.profiler.runtime.timers.TimerService;
import hu.oktech.profiler.runtime.timers.TimerUtils;
import hu.oktech.profiler.util.PropertyUtils;

import java.lang.instrument.Instrumentation;
import java.util.Properties;

/**
 * GC sampling controller
 * 
 * @author Istvan Soos
 */
public class GCSamplingController extends AbstractTimerController {

    public String execute(String command) {
        return null;
    }

    public void init(String config, Instrumentation instrumentation) {
        Properties props = PropertyUtils.parse(config, "\\;");

        long millis = Long.parseLong(props.getProperty("millis", "20000"));
        String queue = props.getProperty("queue", "default");

        String prefix = props.getProperty("prefix", "basic-");

        String gcTimerName = prefix + "gc";
        String dumpTimerName = prefix + "dump";

        TimerService timerService = TimerUtils.get();

        if (!timerService.exists(gcTimerName))
            timerService.register(gcTimerName, "sampling-gc", "queue=" + queue + ";millis="
                    + millis);

        if (!timerService.exists(dumpTimerName))
            timerService.register(dumpTimerName, "dump-single", "queue=" + queue + ";millis="
                    + (millis * 5));

        this.timers = new String[] { gcTimerName, dumpTimerName };
    }

}
