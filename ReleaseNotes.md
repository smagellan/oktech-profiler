### 1.1 ###

Released on: 2009-10-02

New features:
  * Instrumentation profiler using Javassist
  * Method summary for tree reports
  * XML output format for tree report
  * Configurable timer (nanotime / thread cpu time)

Unfortunately this breaks a lot compatibility from the previous one:
  * New stream format
  * New stream items
  * Package renaming, class renaming and refactoring
  * A few configuration changes

If you do require help on migration, please contact us, we do not support it at the moment. The new stream format can be converted to XML easily, it will ensure the future stream compatibility.

### 1.0 ###

Released on: 2009-08-18

Features:
  * Sampling profiler, local Java agent, remote JMX agent
  * Basic tree report in text format

Bugfix releases:
  * [1.0.1](ReleaseNotes_1_0_1.md) on 2009-08-21