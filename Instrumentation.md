# Instrumentation #

First we wouldn't wanted to include instrumentation as part of the OKTECH Profiler, but after the initial interest and requests, we did so. We see this feature as valid profiling method, and although there are several open-source instrumentation profilers out there, this might be useful as we can use the more or less the same analysis on the results as in the sampling case.

## Instrumentation concept ##

The basic concept is to modify the method's source code as one of the following scenarios:

```
protected void someMethod() {
    long start = System.nanoTime();
    // ...
    long end = System.nanoTime();
    Trace.report("someMethod()", (end-start) );
}
```

```
protected void otherMethod() {
   Trace.reportStart("otherMethod");
   try {
      // ...
   } finally {
      Trace.reportEnd("otherMethod");
   }
}
```

The actual instrumentation works like the later method: it traces the entry and the exit point and saves the times at those points.

## Instrumentation impact ##

Even the smallest number of instrumented methods have some overhead:
  * The classes must be modified (startup overhead)
  * The runtime includes the trace codes, that creates procedural and memory overhead (runtime overhead)

If the execution trace contains a lot method, it might be that the instrumentation profiling creates instrumentation event objects faster than the Dumper thread is able to consume, causing OutOfMemoryError to occur, so it must be configured with proper oversight.

## Pros and cons ##

Instrumentation profiling has the following advantages:
  * It can measure exact method times
  * The statistics doesn't contain randomness

Instrumentation profiling has the following disadvantages:
  * The overheads cannot be controlled (at the moment)
  * There is a systematic error in the results

You might be interested in the [Sampling vs Instrumentation](SamplingVsInstrumentation.md) comparison.