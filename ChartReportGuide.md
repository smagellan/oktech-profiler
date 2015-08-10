# Introduction #

Chart reports can help visualize the memory use and network traffic of the profiled application. The report tool generates an html document that displays the data with the help of Google Chart API.

# Generating the report #

| **Property** | **Values** | **Default** | **Description** |
|:-------------|:-----------|:------------|:----------------|
| input        |            |             | Comma separated list of dump files. |
| chart.heap.enabled | boolean    | true        | Enables the line chart showing the allocated heap memory over time |
| chart.heap.enabled | boolean    | true        | Enables the line chart showing the allocated non heap memory over time |
| chart.network.enabled | boolean    | true        | Enables the bar chart showing the amount of transmitted data over time |
| method.filter | string     |             | Only includes network traffic data if the stack trace includes the filtered class, method, for the filter rules refer to [LocalJavaAgent](LocalJavaAgent.md). |

# Example #

To generate a report that shows the network traffic  between the profiled application and an Oracle database server use the following command:

```
   java -cp hu.oktech.profiler.jar hu.oktech.profiler.report.chart.ChartReport input=mydump.dump method.filer=oracle.jdbc.*
```

## Possible result ##

<img src='http://chart.apis.google.com/chart?chs=800x300&cht=lc&chxt=y,x,y&chxl=1:|11:49:44:464|11:51:06:428|11:52:28:392|11:53:50:357|11:55:12:321|2:||[MiB]|&chxr=0,0,114&chtt=Java Heap Memory&chds=0,114&chd=t:14,12,9,10,14,18,16,16,26,38,54,17,36,38,35,53,39,45,47,50,52,56,66,49,52,57,57,60,56,57,64,68,80,58,65,48,63,67,80,82,81,76,85,88,78,81,82,85,93,91,83,96,101,108,111,114,76,81,87,90,99,82,94,107,94,81,93,102,92,80,91,102,111,81,93,107,76,87,98,107,80,89,100,111,82,94,103,77,88,98,109,84,94,105,83,96,89,84,93,88,83,92,88,82,92,88,82,94,88,84,95,90,87,97,92,90,84,90,99,82,94,79,90,99,86,95,81,92,77,89,99,86,97,83,94,79,94,81,92,89,88,87,85,96,82,91,91,88,86,85,94,93,90,88,87,96,82,92,88,93,91,89,98,84,94,92,89,99,85,95,80,91,100,86,96,80,91,101,89,87,86,96,94,91,88,86,96,94,91,88,86,96,94,91,88,87,96,94,92,88&nonsense=heapchart.png' alt='Java Heap Memory' />

<img src='http://chart.apis.google.com/chart?chs=800x300&cht=lc&chxt=y,x,y&chxl=1:|11:49:44:464|11:51:06:428|11:52:28:392|11:53:50:357|11:55:12:321|2:||[MiB]|&chxr=0,0,58&chtt=Java Non Heap Memory&chds=0,58&chd=t:6,7,7,10,13,14,16,18,19,20,21,21,22,24,25,27,27,27,27,27,27,27,28,28,30,31,32,32,32,32,34,36,38,39,41,42,42,42,43,44,45,46,47,48,49,49,49,50,50,52,52,53,53,53,53,53,54,54,54,54,54,55,55,55,55,55,55,55,55,55,55,55,55,55,55,55,55,55,55,55,55,55,55,55,55,55,55,55,55,56,56,56,56,56,56,56,56,56,56,56,56,56,56,56,56,56,56,56,56,56,56,56,56,56,56,57,57,57,57,57,57,57,57,57,57,57,57,57,57,57,57,57,57,57,57,57,57,57,57,57,57,57,57,57,57,57,57,57,57,57,57,57,57,57,57,57,58,58,58,58,58,58,58,58,58,58,58,58,58,58,58,58,58,58,58,58,58,58,58,58,58,58,58,58,58,58,58,58,58,58,58,58,58,58,58,58,58,58,58,58&nonsense=nonheapchart.png' alt='Java Non Heap Memory' />

<img src='http://chart.apis.google.com/chart?chs=800x300&cht=bvg&chxt=y,x,y&chxl=1:|11:50:48:820||||||||||||||||||||||||||||||||||||||||11:51:57:920||||||||||||||||||||||||||||||||||||||||11:53:07:21||||||||||||||||||||||||||||||||||||||||11:54:16:122||||||||||||||||||||||||||||||||||||||||11:55:25:222||||||||||||||||||||||||||||||||||||||||2:||[KiB]|&chxr=0,0,89&chtt=Network traffic&chds=0,89&chbh=4,0,0&chd=t:5,0,55,0,0,0,0,0,7,0,0,0,1,0,20,0,0,0,0,1,3,1,4,4,0,6,4,0,8,4,4,2,47,58,76,89,9,17,8,16,18,17,16,17,9,8,17,16,16,18,16,10,16,8,17,16,16,17,17,2,16,16,17,17,16,16,13,13,7,17,16,17,17,9,7,18,17,17,16,17,17,16,0,17,17,16,17,16,16,2,16,16,18,17,16,17,0,17,17,16,16,18,16,0,17,16,18,16,17,17,9,8,17,17,17,17,17,16,1,17,16,17,17,18,17,8,8,18,17,16,17,17,18,0,17,17,17,16,17,16,1,17,16,17,17,17,16,11,6,17,17,17,16,18,16,0,18,17,17,17,17,11,15,8,17,16,17,17,16,17,1,18,17,17,16,17,17,0,17,18,16,16,17,17,0,17,17,17,18,17,17,16,1,17,16,16,17,17,16,1&nonsense=network.png' alt='Network traffic' />