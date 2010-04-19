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
package hu.oktech.profiler.runtime;

import hu.oktech.profiler.runtime.controller.ControllerUtils;
import hu.oktech.profiler.runtime.hook.LocalJmxServerHook;
import hu.oktech.profiler.runtime.hook.PollFileHook;
import hu.oktech.profiler.runtime.jmx.JmxConnectionUtils;
import hu.oktech.profiler.runtime.queues.QueueUtils;
import hu.oktech.profiler.runtime.timers.TimerUtils;
import hu.oktech.profiler.util.PropertyUtils;

import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

/**
 * The generic profiler agent that registers the runtime components as JMX MBeans and possibly
 * executes controllers.
 * 
 * @author Istvan Soos
 * 
 */
public class Profiler {

    public static void main(String[] args) throws Exception {
        System.out.println("OKTECH-Profiler Application starting...");

        // parsing configuration options
        Properties props = new Properties();
        props.putAll(System.getProperties());
        props.putAll(PropertyUtils.parse(args));

        init(props);
    }

    public static void premain(String agentArgument, Instrumentation instrumentation)
            throws Exception {
        System.out.println("OKTECH-Profiler Agent starting...");

        // instrumentation
        ControllerUtils.get().setInstrumentation(instrumentation);

        // parsing configuration options
        Properties props = new Properties();
        props.putAll(System.getProperties());
        props.putAll(PropertyUtils.parse(agentArgument, "\\:"));

        // this hook started because the Glassfish application server internal
        // initialization of the platform mbean server collided with the
        // Profiler Agent
        if (checkInitHook(props)) {
            init(props);
            System.out.println("OKTECH-Profiler Agent started...");
        } else {
            hookedInit(props);
            System.out.println("OKTECH-Profiler Agent hook initialized...");
        }
    }

    protected static boolean checkInitHook(Properties props) {
        if ("1".equals(props.getProperty("hook", "0")))
            return false;

        try {
            ManagementFactory.getPlatformMBeanServer();
            return true;
        } catch (Throwable ex) {
            return false;
        }
    }

    protected static void hookedInit(final Properties props) {
        Runnable init = new LocalJmxServerHook(props);

        final String pollFile = props.getProperty("hook.poll");
        if (pollFile != null)
            init = new PollFileHook(pollFile, props);

        Thread t = new Thread(init, Profiler.class.getName() + " hook thread");
        t.setDaemon(true);
        t.start();
    }

    public static void init(Properties props) throws MBeanRegistrationException,
            NotCompliantMBeanException, MalformedObjectNameException {
        // MBean registration
        MBeanServer mbeanServer = JmxConnectionUtils.get().getLocal();
        if (mbeanServer == null) {
            System.err
                    .println("Local JMX server is not present, skipping OKTECH Profiler JMX registration...");
        } else {
            try {
                mbeanServer.registerMBean(JmxConnectionUtils.get(), new ObjectName(
                        "hu.oktech.profiler.runtime:name=JmxConnectionService"));
            } catch (InstanceAlreadyExistsException e) {
            }
            try {
                mbeanServer.registerMBean(QueueUtils.get(), new ObjectName(
                        "hu.oktech.profiler.runtime:name=QueueService"));
            } catch (InstanceAlreadyExistsException e) {
            }
            try {
                mbeanServer.registerMBean(TimerUtils.get(), new ObjectName(
                        "hu.oktech.profiler.runtime:name=TimerService"));
            } catch (InstanceAlreadyExistsException e) {
            }
            try {
                mbeanServer.registerMBean(ControllerUtils.get(), new ObjectName(
                        "hu.oktech.profiler.runtime:name=ControllerService"));
            } catch (InstanceAlreadyExistsException e) {
            }
        }

        // controllers
        Set<String> controllers = new LinkedHashSet<String>();

        // pre-populate 'controllers' property
        if ("1".equals(props.getProperty("sampling"))) {
            controllers.add("sampling-thread");
            controllers.add("sampling-memory");
            controllers.add("sampling-gc");
        }

        // Controller prop initialization
        String controllersProp = props.getProperty("controllers");
        if (controllersProp != null) {
            String[] csplit = controllersProp.split("\\,");
            for (String cname : csplit) {
                cname = cname.trim();
                if (cname.length() == 0)
                    continue;
                controllers.add(cname);
            }
        }

        // do the registration
        for (String cname : controllers) {
            // getting controller config
            String ctype = props.getProperty(cname + ".type", cname);
            String config = props.getProperty(cname + ".config", "");

            // registering controller
            ControllerUtils.get().register(cname, ctype, config);

            // starting controller
            ControllerUtils.get().start(cname);
        }
    }
}
