# Local Java Agent #

The local Java agent is registered at the startup of the application and it can start sampling and/or do instrumentation.

## Startup ##

To run the profiler in the same JVM, you shall execute the your application (or application server) with the java-agent parameter:

```
java -javaagent:hu.oktech.profiler-runtime.jar [other parameters]
```

If the default configuration options does not apply, you might specify configuration parameters in the following, comma-separated way:

```
java -Dkey0=value0 -javaagent:hu.oktech.profiler-runtime.jar=key1=value1,key2=value2,prop=other.properties [...]
```

You can set property values as:
  * Java system properties (-Dkey0=value0)
  * Java agent parameters (...jar=key1=value1)
  * External properties file (prop=other.properties)

## Configuration ##

You can set the following configuration options for the local jmx runtime.

**Local runtime properties**

| **Property** | **Values** | **Description** |
|:-------------|:-----------|:----------------|
| instr.class  | filter rule (1) | Set this property if you would like to turn on instrumentation. The filter rule specifies the classes that will be instrumented. |
| instr.method | filter rule (1) | Fine grained filter for setting separate methods. Must contain full qualified name. |
| instr.traceWorker | nano | threadcpu | nano will use only the System.nanoTime(), threadcpu will use JMX's thread cpu time too (2) |

**Common properties**

| **Property** | **Values** | **Description** |
|:-------------|:-----------|:----------------|
| timers       | comma separated classes | The classes that are subclasses of AbstractTimer will be created and initiated. The defaults include the ThreadSampler, the MemorySampler and the Dumper. |
| sampling.millis | long       | The general sampling millisec (will be used as default if no specific is given) |
| sampling.nanos | long       | The general sampling nanosec (will be used as default is no specific is given) |
| thread.sampling.millis | long       | The sampling millis for ThreadSampler |
| thread.sampling.nanos | long       | The sampling nanos for ThreadSampler |
| thread.sampling.threadCpu | boolean    | True to include JMX's thread cpu time, false to use just nanoTime() (2) |
| memory.sampling.millis | long       | The sampling millis for MemorySampler |
| memory.sampling.nanos | long       | The sampling nanos for MemorySampler |
| dumper.sampling.millis | long       | The sampling millis for Dumper |
| dumper.sampling.nanos | long       | The sampling nanos for Dumper |
| output.file  | string     | The output file for the dump, if not specified, auto name generation is applied. |
| output.auto.dateformat | string     | The date format that will give the middle of the dump file. |
| output.auto.prefix | string     | The prefix of the auto file name |
| output.auto.postfix | string     | The postfix of the auto file name |

(1) The rule processing is the following:
  * if filter is specified, the default rule is exclude
  * rules are separated by comma
  * rules are executed in the order defined in the filter
  * leading '-' character means exclude, leading '+' means include, neither means include
  * '!' at the end of the rule means that include-exclude decision will be made at that point
  * the remaining part of the rule is processed as a regex pattern, so hu.oktech.**is not really precise, you shall use hu\.oktech\..** instead, but in most of the cases, it will be just fine without escapes

(2) JMX's thread cpu time is about 300 times slower (in the millisec range) than the nanoTime() (in the nanosec range).

## Pros and cons ##

The advantages of local Java agent:
  * Instrumentation profiler is only enabled this way
  * Easy to setup, no remote JMX server is required

The disadvantages of local Java agent:
  * Sampling works from the beginning to the end (will be addressed in the future versions)
  * Cannot profile already running applications