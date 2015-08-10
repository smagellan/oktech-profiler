# OKTECH Profiler features #

This is just a summary of the actual features of the profiler.

## Profiling technique ##

OKTECH Profiler is a sampling and instrumentation profiler. As a sampling profiler it provides:
  * Application profiling without code modification
    * Code modification introduces risk of breaks
    * Code instrumentation has a systematic error
  * Periodically samples the JVM through local or remote JMX connection
    * Checks the running threads, their state, actual stack trace
    * Checks the used and available memory
  * It has different characteristics than the instrumentation profilers, but still produces valuable analytic data
As an instrumentation profiler it provides:
  * Precise, method-level time measurements
  * Full execution-path tracking on the profiled methods
  * Works only as local Java agent

It stores the profiling information in a single binary file:
  * This allows remote analysis or remote profiling
    * There is no need for permanent connection to the profiled application
    * If there are tight rules, only the infrastructure team is allowed to touch the production system, meaning you have only a little possibility that they will setup the profiler there, and if the setup is complicated, they won't do it
    * You do not loose your data if you accidentally close the controller application
  * The append-only writing overhead of local files are typically less than any other communication method, and because there is no local processing, the CPU overhead is negligible.

This file can be converted to XML for future compatibility.

## Profiler runtime ##

The profiler can be used as a Java agent:
  * Allows you to easily integrate with any Java 5 (or later) application, including application servers and desktop applications
  * Allows you to easily profile batch jobs, e.g. JUnit tests
    * you can setup the profiling instructions before the execution begins and you don't require a management console
    * Profiling as part of the build process is just another good thing it helps you to track performance

The profiler can be used as remote JMX client:
  * Allows you to connect already running applications (if they were JMX-enabled)
  * This is a requirement for profiling "never touch it" production environments


## Reports and analysis ##

At the moment, we are short on this, but you might expect more if you request a new report feature.

Execution-path (tree) report:
  * currently available in text and XML format
  * traces the execution path of the threads
  * counts the method occurrences in the position of the execution path
  * sampling profiler: estimates the time spent in methods (it is rather a ration than an actual time, but still useful)
  * instrumentation profiler: measures the time spent in methods
  * output is ordered, most time-consuming methods are listed first
  * filters can be applied on the result, only the interesting part is shown

Execution-summary (tree-summary) report:
  * summarizes the separate items in the execution path
  * gives details about entry points (from where the actual method has been called) and exit points (which methods were called form that point)