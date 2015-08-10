OKTECH Profiler is a sampling and instrumentation profiler now. This page will highlight the advantages and drawbacks of each profiling technique and helps you to decide which way to go.



# Sampling vs Instrumentation #

The following table quickly summarizes the basic conceptual differences:
<table cellpadding='6' width='90%' align='center'>
<tr>
<blockquote><td width='20%'></td>
<td width='40%'><b>Sampling</b></td>
<td width='40%'><b>Instrumentation</b></td>
</tr></blockquote>

<tr valign='top'>
<blockquote><td><b>Code modification:</b></td>
<td>Does not modifies the code</td>
<td>Code is modified at bytecode level, typically at load-time, affecting the application startup. HotSpot will not work the same way.</td>
</tr></blockquote>

<tr valign='top'>
<blockquote><td><b>Overhead:</b></td>
<td>Only when sampling. Overhead can be tuned with the sampling frequency.</td>
<td>Large overhead at bytecode modification, small but constant overhead while running. Overhead increases with the number of methods instrumented and with the number of threads running (the later depends on the implementation).</td>
</tr></blockquote>

<tr valign='top'>
<blockquote><td><b>Accuracy:</b></td>
<td>Only statistical analysis is possible with an ever-existing, but (with more frequent sampling) decreasing and small error ratio. Estimates can be applied.</td>
<td>Considered to be accurate, but only with a systematic error (caused by embedded instructions and the presence of HotSpot engine) that creates a distortion in the otherwise trusted results.</td>
</tr></blockquote>

<tr valign='top'>
<blockquote><td><b>Repeatability:</b></td>
<td>Different sessions might produce different results.</td>
<td>Different sessions typically produce similar results.</td>
</tr></blockquote>

</table>

## Code modification ##

Sampling profilers does not modify the code at all, therefore they leave the application in a state that is just the same is it will be in production, without any profiler.

Instrumentation profilers modify the Java bytecode. Even if this is 4-5 ms for a single method, it can consume a lot time if it is applied for thousands of methods. To reduce the time required, one can apply caching for the instrumentation; e.g. storing the instrumented classes and if they are not changed by next time it is instrumented, it will use the cache instead. Furthermore this bytecode modification will introduce different execution path in the application, not allowing the HotSpot engine to optimize the code as good as it would be without the instrumentation codes.

## Overhead ##

Sampling profiler introduces only the overhead of reading the system information in the actual sampling step, e.g. reading the available memory size or creating a stack trace of all threads. This is a relatively small overhead compared to the overall runtime of the application, which can be tuned by modifying the sampling frequencies: smaller frequency will produce more overhead, while rare frequency will produces negligible overhead.

Instrumentation profiler's startup overhead is already discussed, but it is not straightforward what the runtime overhead really means. In the traditional way, an instrumentation profiler modifies the code something like the following:
```
 // before
public void somemethod() {
    doingSomeStuff();
    doingSomeOtherStuff();
}

 // after
public void somemethod() {
    long start = getClockTime();
    try {
        doingSomeStuff();
        doingSomeOtherStuff();
    } finally {
        long end = getClockTime();
        report("somemethod()", end-start);
    }
}
```

Of course every instrumentation might be different, it might just report a start and and end timestamp, or just a code instead of method name, the possibilities are not listed here. The important point is that in each method:
  * It will measure the clock twice
  * It will call some reporting procedure at least once

This overhead seems to be small now, but:
  * It introduces the systematic error / distortion of the instrumentation profilers
  * It is not negligible at all if you need to process the numbers anyhow. For example if you need to write it to a file or stream through the network, it requires synchronization on some structure (e.g. queue).


### The processing problem of instrumentation profilers ###

The problem lies in the fact that measuring the clock is not everything: you need to construct an execution context from it, you need to create summaries, average times, statistical mean and other calculations from it. You have three basic choices:
  * Process (subset of) these information in the same thread - it increases the profiler overhead in the same thread.
  * Process (subset of) these information in a different thread - it introduces a synchronization problem as the infromation must be gathered from various threads, there will be some small mutex or some synchronized block somewhere. Threads will block and wait.
  * Process (subset of) these information in a different process, with e.g. dumping the information to a file or sending it through network - this introduces an even more IO overhead of the application. Threads will block and wait.

Any single or combined route will introduce at least some, but definitely existing and partly measurable overhead. Partly measurable, because with thread cpu clock, one can separate most of the profiler overhead from the worker thread, however there are a few instructions that will do an inter-thread message passing, a clock check or anything like that, which is in the end not separable from the worker thread. And even if you measure the time spent in the profiler block as overhead, there is always an extra reporting method that is not measured (e.g. you must process the time but you can do that after you have measured the time).

## Accuracy ##

Sampling profilers do have an architectural randomness in the results, it is just because how they work. There is no chance to extract the same information this way as it is produced by instrumentation profilers, so one shall use other methods to create certain kinds of report. These reports are usually differently templated, they report different aspects of the application, however statistical analysis and some experimental heuristics can provide estimates, which can be presented in a similar fashion as they are presented in the instrumentation profiler way.

Instrumentation profilers seems to have better accuracy, because they are able to measure the inner parts of the methods pretty good. It is less known that such measurements are usually just a false sense of certainty: they produce a systematic error or reproducible distortion in the results. This error can be negligible if a method is executed only a few times, or if it does take a long time to execute it, but if some method is in the upper regions of the execution path, or it doesn't take too much time to finish, the error might significantly affect the outcome.

### The systematic error of instrumentation profiler's accuracy ###
Let's check the following code, supposed that every mentioned method is profiled:
```
public void a() { while (b()) { ... } }
public boolean b() { for (int i = 0; i < 10; i++) c(); return someBoolean; }
public void c() { d(); }
public void d() { e(); f(); }
public void e() { ... }
public void f() { ... }
...
```

  * At the time we profile method _e()_ and _f()_, we have a little overhead as mentioned above. At least the clock access will be an overhead, but typically many more instructions will be executed in this actual thread.
  * At the time we profile method _d()_, we have the same overhead as in _e()_ and _f()_ just for the method, but on top of that, we will measure the overhead of _e()_ and _f()_ as the part of method _d()_, distorting the result of execution.
  * At the time we profile method _c()_, on top of the usual single-method overhead, we have an even more accumulating overhead of the underlying methods...
  * This overhead accumulates even more in _b()_ and _a()_, with loops and repeated execution, completely eliminating the possibility to treat it as a precise measurement number

There are (so far only) theoretical options to measure the profiler codes first and subtract them from the measurements. Seems to be good and could solve the above-mentioned problem, but the HotSpot engine's effect is still there:
  * With the instrumentation, the code is changed, period. The HotSpot will optimize the code differently, it will not inline some methods, it will produce slightly different native codes in some cases. The execution times will be (at least) different.
  * Due to the previous point, there is no guarantee that the measured and to-be subtracted profiler codes are the same everywhere in the application.
  * It will consume a lot system/real time to process and substract all the profiler times.

## Repeatability ##

Sampling profilers do have an architectural randomness in the results, making it unlikely to reproduce the same results. The [Law of large numbers](http://en.wikipedia.org/wiki/Law_of_large_numbers) however helps to improve the uncertainty of the results.

Instrumentation profilers tend to reproduce similar results, this is a definite advantage for them.

# Use-case recommendations #

It is hard to make generic comments on when this or that kind of profiler shall be used, however there are typical requirements that might help to decide:

<table cellpadding='6'>
<tr>
<blockquote><td width='70%'><b>Use case</b></td>
<td width='30%'><b>Optimal choice</b></td>
</tr></blockquote>

<tr>
<blockquote><td>Are you looking for synchronization problems or waits?</td>
<td><b>Sampling</b></td>
</tr></blockquote>

<tr>
<blockquote><td>Are you profiling a production application?</td>
<td><b>Sampling</b></td>
</tr></blockquote>

<tr>
<blockquote><td>Are you looking for performance bottlenecks?</td>
<td><b>Sampling and Instrumentation</b></td>
</tr></blockquote>

<tr>
<blockquote><td>Do you require precise times on the profiled methods?</td>
<td><b>Instrumentation</b></td>
</tr></blockquote>

</table>