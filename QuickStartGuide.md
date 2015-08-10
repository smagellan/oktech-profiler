# Quick Start Guide to OKTECH Profiler #

This guide shortly describes the basic use of OKTECH Profiler. For in-depth details, please refer to the [documentation](Documentation.md).



# Profiling the application #

First, you must profile the application and collect information from it. You can profile your application in the following ways:
  * _Java agent_: Run the profiler in the same JVM as the application, specifying the profiler parameters at the start. You can use sampling and/or instrumentation.
  * _Remote JMX_: Run the profiler in a separate process, collect the information through JMX. You can use sampling only.

## Local Java agent ##

To run the profiler in the same JVM, you shall execute your application (or application server) with the -javaagent parameter. The default configuration will start [thread](ThreadSampling.md) and [memory](MemorySampling.md) sampling in every second, and the information is written to a file in the tmp/profiler/`[`timestamp`]`.dump format.

```
java -javaagent:hu.oktech.profiler-runtime.jar [other parameters]
```

You can easily reconfigure the [thread sampling](ThreadSampling.md) period to 10 sec as follows (more on that in the [local Java agent configuration options](LocalJavaAgent.md)):

```
java -javaagent:hu.oktech.profiler-runtime.jar=thread.sampling.millis=10000 [other parameters]
```

To turn on [instrumentation](Instrumentation.md), you can do the following  (more on that in the [local Java agent configuration options](LocalJavaAgent.md)):

```
java -javaagent:hu.oktech.profiler-runtime.jar=instr.class=+com.yourcompany.*!,-.* [other parameters]
```

## Remote JMX client ##

In case you would like to profile an already-running application, you can connect to its JMX server with the following way:

```
java -jar hu.oktech.profiler-runtime.jar [other parameters]
```

Of course JMX requires some parameters: (more on that in the [remote JMX connection options](RemoteJmxConnection.md))

```
java -jar hu.oktech.profiler-runtime.jar \
   remote.jmx.url=service:jmx:rmi:///jndi/rmi://production.server.company.com:9998/jmxrmi \
   remote.jmx.user=admin \
   remote.jmx.password=adminadmin
```

The [thread sampling](ThreadSampling.md) example as above is the same, however you are not able to use instrumentation this way.

# Reports #

At the moment the reporting capability is limited to a tree-based execution-trace report, however it can applied to both sampling and instrumentation dumps.

You might modify the dump stream before the report (e.g. before sending over the network), or during it.

## Modify the dump stream ##

You can [filter and/or convert](InformationDump.md) the dump format to better fit your needs. The filtering can be applied in the report as well.

```
java -cp hu.oktech.profiler-report.jar \
    hu.oktech.profiler.core.stream.StreamConverter \
    input.file=2009-09-28-15-53-28.dump \
    output.file=2009-09-28-15-53-28.xml
```

You might want to filter out methods and from the stream, that way it can be more compact and the report format is not that verbose.

## Tree report ##

The [tree report](TreeReport.md) produces a simple tree that shows:
  * thread execution trace with counts
  * cpu times and system times (with estimates at the thread sampling)
  * with percentage calculations compared to the parent methods
  * summary of each method, outside of the execution trace

The report can produce text and XML output. You can fine-tune both the input stream processing (as in the previous section) or adjust the output settings (e.g. minimum counts, separate execution tree by thread).

### Text tree output ###

You can execute the text tree report like the following command. It will read the specified files, creates analysis from each as separate items and merges them.

```
java -jar hu.oktech.profiler-report.jar input=my-server-1.dump,my-server-2.dump
```

### XML tree output ###

The XML output is the same as the text output (might contain a bit more info), and its role is to allow our users to produce customized output if they would like to.

```
java -cp hu.oktech.profiler-report.jar hu.oktech.profiler.report.text.TreeTextReport -input=my-server-1.dump
```