# Information dump #

The various parts of OKTECH Profiler produce pieces of information about the system. It is not written out at the same time it is produced, as it would introduce a few problem around thread synchronization, file operations and distortion of the results. Instead, they are appended to a queue that is consumed by the Dumper thread. The Dumper thread is responsible to dump these information to a given file with the most efficient way (at the moment, with DataOutputStream).

## The dump format ##

The dump is a binary format, because speed and efficiency was the highest priority here. Each item in the stream is responsible to serialize and de-serialize  itself from this stream, however for more compatibility and extensibility we have introduced an XML stream format.

One can convert between the binary and XML format like the following command (the format is autodetected from the extensions):

```
java -cp hu.oktech.profiler-report.jar \
    hu.oktech.profiler.core.stream.StreamConverter \
    input.file=2009-09-28-15-53-28.dump \
    output.file=2009-09-28-15-53-28.xml
```

You can use the following configuration setting to influence the output dump format:

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