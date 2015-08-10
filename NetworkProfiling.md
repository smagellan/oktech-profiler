# Quick Start Guide to OKTECH Profiler network traffic profiling capability #

Network traffic can only be profiled if the profiler is run with the javaagent JVM parameter while setting the network.prof.enabled property to true. The feature is disabled by default.

```
java -javaagent:hu.oktech.profiler-runtime.jar=network.prof.enabled=true [other parameters]
```

# Examples #
## Eclipse RCP Application ##

Eclipse RCP applications have an .ini file in their root folder with the same name as the application executable. Java agent has to be set as the first JVM argument after -vmargs, an example ini file:

```
--launcher.XXMaxPermSize 256M
-framework
plugins\org.eclipse.osgi_3.4.0.v20080605-1900.jar
-vmargs
-javaagent:hu.oktech.profiler.jar=network.prof.enabled
-Duser.name=john.doe    
-Dosgi.requiredJavaVersion=1.5
-Xms40m
-Xmx512m
```

## JBoss ##

The following instructions were tested JBoss 4.2.3GA:

The JAVA\_OPTS environment variable has to be set before starting the server, windows example:

```
>set JAVA_OPTS=-javaagent:hu.oktech.profiler.report.jar=network.prof.enabled
>run -b 0.0.0.0 -c my-profile
```

# How it works #

Network profiling uses a java agent so that it can register its own socket factory, for details refer to http://www.j2ee.me/j2se/1.5.0/docs/api/java/net/Socket.html#setSocketImplFactory%28java.net.SocketImplFactory%29. Because of this network profiling shouldn't be used with applications that register their own socket factory implementation.

# Reports #

Network traffic information is included in reports by default. One can disable it in text and xml reports by setting the report.socket.tree property to false. Network traffic information can be omitted from chart reports similarly by setting the .
For more information on how to generate reports please refer to [QuickStartGuide](QuickStartGuide.md) or [ChartReportGuide](ChartReportGuide.md).