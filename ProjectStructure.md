# Project structure #

The project contains only a single project at the moment (`*`). You can checkout the sources [here](http://code.google.com/p/oktech-profiler/source/checkout). It follows the usual Java project structure and it contains an Eclipse project too.

(`*`) There was a short time period when we have evaluated maven for the build and dependency management, created separate projects for the various parts, but it failed in many aspects (IDE integration, source codes doesn't necessarily exists in the repository), so we have dismissed the maven for now.

## Packages ##

The packages are important, because the bundle ant task decides the exclusion of packages based on the package name, so if you place something in the wrong package, it might be removed at that point from the jars.

There is three parts in the project: the runtime (doing the sampling and/or instrumentation), the reporting (doing the analysis and the formatting) and the common classes:
  * hu.oktech.profiler.core is the place for common classes
  * hu.oktech.profiler.runtime is the place of runtimes
  * hu.oktech.profiler.analyzer contains the now-only analyzer utilities
  * hu.oktech.profiler.report contains the formatting / managing parts for the reports (actually there is an overlap with the analyzer stuff in this regard)

## Build ##

You might execute **ant all** to compile, package, junit test and bundle the project, however for most of the cases **ant bundle** will be just fine, it skips the junit test part.

At the end of the build process, you will receive two jars:
  * hu.oktech.profiler-runtime.jar contains the codes and dependent libraries that are needed for the runtime
  * hu.oktech.profiler-report.jar contains the codes and dependent libraries that are needed for the analyzer and reports