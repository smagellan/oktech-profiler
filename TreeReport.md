# Tree Report #

From the stack traces or from the events reported by instrumentation, you can create the full execution tree that displays the various execution paths of the application - and this is what we call tree report. The tree report displays:
  * The summary of all single stack traces (either by sampling or by instrumentation)
  * The items are ordered in a descending relevance (e.g. real time spent in the actual method)
  * You can browse all stack path, but you will receive a per-method summary that contains the summarized data for the given method, and it contains separate reports for the entry and exit paths too.

## Text tree report ##

The text tree report will produce a simple, textual report that can be viewed on a console or in any text-viewer or editor. The general configuration of the report follows our 'usual' pattern:

```
java -Dkey0=value0 -jar hu.oktech.profiler-report.jar key1=value1 prop=other.properties
```

You can set property values as:
  * Java system properties (-Dkey0=value0)
  * Normal application parameters (key1=value1)
  * External properties file (prop=other.properties)

A simple example:

```
java -jar hu.oktech.profiler-report.jar input=my-server-1.dump,my-server-2.dump
```

The output is printed on the stdout, you might redirect it to a file your choice. You can check the configuration properties later on this page.

## XML tree report ##

The XML tree report will produce a simple XML report that can be viewed and/or processed by further tools. At the moment we only produce this report, it is up to you how you proceed with it. The general configuration of the report follows our 'usual' pattern (check above), the difference is that you execute it differently:

```
java -cp hu.oktech.profiler-report.jar hu.oktech.profiler.report.xml.TreeXmlReport -input=my-server-1.dump
```

The output is printed on the stdout, you might redirect it to a file your choice. You can check the configuration properties later on this page.

## Configuration ##

**Common configuration options applied to both reports:**

| **Property** | **Values** | **Description** |
|:-------------|:-----------|:----------------|
| input        | comma separated file list | Each of these files will be loaded and processed separately and only the end result is merged into a single report. This is required because there are items (e.g. time estimates, instrumentation events) that are bound to a single session or file. |
| report.sampling.tree | boolean    | Set to false if you would like to skip the sampling tree from the report. |
| report.sampling.summary | boolean    | Set to false if you would like to skip the sampling summary from the report. |
| report.instr.tree | boolean    | Set to false if you would like to skip the instrumentation tree from the report. |
| report.instr.summary | boolean    | Set to false if you would like to skip the instrumentation summary from the report. |
| sampling.thread.nameInStackTrace | boolean    | Set to true to separate methods by name. |
| output.method.filter | filter rule (1) | This filter rule specifies what to include in the result. |
| output.method.minCounter | integer    | The minimum number of measurement counts that will be in the result. |

**Common configuration options applied to the input stream filtering:**

| **Property** | **Values** | **Description** |
|:-------------|:-----------|:----------------|
| method.filter | filter rule (1) | The filter rule specifies which classes/methods are included. |
| sampling.method.filter | filter rule (1) | This filter rule specifies which classes/methods from the sampling are included. It modifies the stacktrace of the samples, removes the elements that are not included. If not set, defaults to method.filter. |
| instr.method.filter | filter rule (1) | This filter rules specifies which classes/methods from the instrumentation are included. It dismisses the events that are not included. If not set, defaults to method.filter. |
| sampling.thread.filter | filter rule (1) | This filter rule specifies which threads from the sampling are included. It removes the whole sample if it is not included. |

(1) The rule processing is the following:
  * if filter is specified, the default rule is exclude
  * rules are separated by comma
  * rules are executed in the order defined in the filter
  * leading '-' character means exclude, leading '+' means include, neither means include
  * '!' at the end of the rule means that include-exclude decision will be made at that point
  * the remaining part of the rule is processed as a regex pattern, so hu.oktech.**is not really precise, you shall use hu\.oktech\..** instead, but in most of the cases, it will be just fine without escapes