# Remote JMX Connection #

In case you would like to profile an already-running application, you can connect to its JMX server and do e.g. thread sampling through the network. This allows the monitoring of already running applications.

## Startup ##

To run and connect to the remote JVM, you shall execute the profiler the following way:

```
java -jar hu.oktech.profiler-runtime.jar [other parameters]
```

If the default configuration options does not apply, you might specify configuration parameters in the following, comma-separated way:

```
java -Dkey0=value0 -jar hu.oktech.profiler-runtime.jar  key1=value1 key2=value2 prop=other.properties [...]
```

You can set property values as:
  * Java system properties (-Dkey0=value0)
  * Normal application parameters (key1=value1)
  * External properties file (prop=other.properties)

## Configuration ##

You can set the following configuration options for the remote JMX runtime.

**Remote runtime properties**

| **Property** | **Values** | **Description** |
|:-------------|:-----------|:----------------|
| remote.jmx.url | string     | The JMX server's URL |
| remote.jmx.user | string     | The remote server's username (optional) |
| remote.jmx.password | string     | The remote server's password (optional) |

**Common properties**

| **Property** | **Values** | **Description** |
|:-------------|:-----------|:----------------|
| timers       | comma separated classes | The classes that are subclasses of AbstractTimer will be created and initiated. The defaults include the ThreadSampler, the MemorySampler and the Dumper. |
| sampling.millis | long       | The general sampling millisec (will be used as default if no specific is given) |
| sampling.nanos | long       | The general sampling nanosec (will be used as default is no specific is given) |
| thread.sampling.millis | long       | The sampling millis for ThreadSampler |
| thread.sampling.nanos | long       | The sampling nanos for ThreadSampler |
| thread.sampling.threadCpu | boolean    | True to include JMX's thread cpu time, false to use just nanoTime() (1) |
| memory.sampling.millis | long       | The sampling millis for MemorySampler |
| memory.sampling.nanos | long       | The sampling nanos for MemorySampler |
| dumper.sampling.millis | long       | The sampling millis for Dumper |
| dumper.sampling.nanos | long       | The sampling nanos for Dumper |
| output.file  | string     | The output file for the dump, if not specified, auto name generation is applied. |
| output.auto.dateformat | string     | The date format that will give the middle of the dump file. |
| output.auto.prefix | string     | The prefix of the auto file name |
| output.auto.postfix | string     | The postfix of the auto file name |

(1) JMX's thread cpu time is about 300 times slower (in the millisec range) than the nanoTime() (in the nanosec range).

## Pros and cons ##

The advantages of remote JMX profiling:
  * Sampling is fine tuned, works only while the remote runtime is connected
  * Can profile already running applications

The disadvantages of remote JMX profiling:
  * Instrumentation profiler is not enabled
  * Remote JMX server is required
  * Network connection is overhead