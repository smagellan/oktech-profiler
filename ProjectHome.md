# OKTECH Profiler #

OKTECH Profiler is a low-impact, sampling and instrumentation profiler for Java. It doesn't require constant connection from a profiler console, as it dumps the profiled information in a binary file. This file might be processed any time later on; report can be generated from the overall information. It can be executed in a local JVM or it can connect to a remote JMX server, allowing to profile any kind of application, e.g.:
  * Benchmarking unit tests during the build process
  * Production application performance analysis in application servers
  * Custom profiling setup for desktop, client-server or any, JMX-enabled Java application

The overhead of the profiler can be very low (and it can be tuned further if required, e.g. reducing the sampling frequency), but to improve accuracy it can be used in an instrumentation mode also, with a slightly higher overhead.

Start with the following page:
  * [Quick Start Guide](QuickStartGuide.md)
  * [Features](Features.md)
  * [Release notes](ReleaseNotes.md) (**1.1 is out - 2009-10-02**)
  * [Downloads](http://code.google.com/p/oktech-profiler/downloads/list)
  * [Documentation](Documentation.md)

  * [License information](License.md)
  * [Support](Support.md) (community and commercial)
  * [Donate](Donate.md)

OKTECH Profiler is the product of [OKTECH-Info Kft.](http://oktech.hu/)