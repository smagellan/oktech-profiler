# Thread Sampling #

Thread sampling is the major factor behind OKTECH Profiler, as - at the time we have open-sourced it - we haven't found any decent, open-source Java profiler that did such sampling.

## The thread sampling concept ##

Thread sampling periodically asks the JVM for the list of stacktraces for all active threads. On top of that we receive a few more information, but the stacktrace is the most important part of it. Such sampling can be done at any rate of frequency, from milliseconds to minutes, therefore the user has really great influence how the profiling impacts the running application.

If the application spends more time in a method, it will be more probable that it will occur in the sampled stacktraces. The more sample we take, the results will be increasingly accurate.

## Estimating the times ##

Let's assume we sample a thread in sequence. The stacktrace will probably differ from time to time. We cannot be sure if we remained or we have returned and re-entered in the methods we see. From this reason it is a great question if, how and with what error rate can we estimate the running times with the profiler.

At the moment we do this as follows:
  * Build the full execution tree from the traces
  * Check the time spent between the samples on a given thread
  * Distribute the time spent on the probability of the given stacktrace chain. This means that from the parent's perspective if there is a variety of children, they will receive less time, as the parent's time is distributed among them.

We will investigate this topic with statistical apparatus sooner or later.

## Pros and cons ##

The advantages of thread sampling:
  * Adjustable, usually small overhead
  * No application modification is required

The disadvantages of thread sampling:
  * No exact times
  * Not easy to trust such statistics

You might be interested in the [Sampling vs Instrumentation](SamplingVsInstrumentation.md) comparison.