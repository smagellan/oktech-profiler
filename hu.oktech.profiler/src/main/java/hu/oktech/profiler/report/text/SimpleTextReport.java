/* 
 * Copyright (c) 2009-2010, OKTECH-Info Kft. - All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Please check attribution requirements at
 * 
 * http://code.google.com/p/oktech-profiler/wiki/License
 * 
 */
package hu.oktech.profiler.report.text;

import hu.oktech.profiler.analysis.sampling.gc.AbstractGCAnalyzer;

import hu.oktech.profiler.analysis.sampling.gc.FormattedTimelineDumpGCAnalyzer;
import hu.oktech.profiler.analysis.sampling.gc.SimpleTimelineDumpGCAnalyzer;
import hu.oktech.profiler.analysis.sampling.memory.AbstractMemoryAnalyzer;
import hu.oktech.profiler.analysis.sampling.memory.SimpleTimelineDumpMemoryAnalyzer;
import hu.oktech.profiler.analysis.sampling.thread.AbstractThreadAnalyzer;
import hu.oktech.profiler.analysis.sampling.thread.ListCounterThreadAnalyzer;
import hu.oktech.profiler.analysis.sampling.thread.TreeCounterThreadAnalyzer;
import hu.oktech.profiler.runtime.io.DumpFileReader;
import hu.oktech.profiler.runtime.sampling.gc.GCData;
import hu.oktech.profiler.runtime.sampling.memory.MemoryData;
import hu.oktech.profiler.runtime.sampling.thread.ThreadData;
import hu.oktech.profiler.util.PropertyUtils;
import hu.oktech.profiler.util.processing.CallbackController;
import hu.oktech.profiler.util.processing.ObjectCallback;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

/**
 * Simple text report that generates a few text files
 * 
 * @author Istvan Soos
 * 
 */
public class SimpleTextReport {

    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.putAll(System.getProperties());
        props.putAll(PropertyUtils.parse(args));

        new SimpleTextReport().report(props);
    }

    protected File[] inputs;

    protected Map<String, AbstractThreadAnalyzer> threadAnalyzers = new HashMap<String, AbstractThreadAnalyzer>();
    protected Map<String, AbstractMemoryAnalyzer> memoryAnalyzers = new HashMap<String, AbstractMemoryAnalyzer>();
    protected Map<String, AbstractGCAnalyzer> gcAnalyzers = new HashMap<String, AbstractGCAnalyzer>();

    public void report(Properties props) throws IOException {
        init(props);
        analyze();
        report();
    }

    protected void report() throws IOException {
        for (Entry<String, AbstractThreadAnalyzer> e : threadAnalyzers.entrySet()) {
            FileWriter out = new FileWriter(("thread." + e.getKey() + ".txt").toLowerCase());
            e.getValue().print(4, out);
            out.flush();
            out.close();
        }
        for (Entry<String, AbstractMemoryAnalyzer> e : memoryAnalyzers.entrySet()) {
            FileWriter out = new FileWriter(("memory." + e.getKey() + ".txt").toLowerCase());
            e.getValue().print(out);
            out.flush();
            out.close();
        }
        for (Entry<String, AbstractGCAnalyzer> e : gcAnalyzers.entrySet()) {
            FileWriter out = new FileWriter(("gc." + e.getKey() + ".txt").toLowerCase());
            e.getValue().print(out);
            out.flush();
            out.close();
        }
    }

    protected void addToThreadAnalyzer(String name, ThreadData data) {
        AbstractThreadAnalyzer ta = threadAnalyzers.get(name + ".tree");
        if (ta == null) {
            ta = new TreeCounterThreadAnalyzer();
            threadAnalyzers.put(name + ".tree", ta);
        }
        ta.add(data);

        ta = threadAnalyzers.get(name + ".tree2");
        if (ta == null) {
            ta = new TreeCounterThreadAnalyzer(true);
            threadAnalyzers.put(name + ".tree2", ta);
        }
        ta.add(data);

        ta = threadAnalyzers.get(name + ".list");
        if (ta == null) {
            ta = new ListCounterThreadAnalyzer();
            threadAnalyzers.put(name + ".list", ta);
        }
        ta.add(data);
    }

    protected void addToMemoryAnalyzer(MemoryData data) {
        AbstractMemoryAnalyzer ma = memoryAnalyzers.get("raw-dump");
        if (ma == null) {
            ma = new SimpleTimelineDumpMemoryAnalyzer();
            memoryAnalyzers.put("raw-dump", ma);
        }
        ma.add(data);
    }

    protected void addToGCAnalyzer(String name, GCData data) {
        AbstractGCAnalyzer gca = gcAnalyzers.get(name + ".raw-dump");
        if (gca == null) {
            gca = new SimpleTimelineDumpGCAnalyzer();
            gcAnalyzers.put(name + ".raw-dump", gca);
        }
        gca.add(data);

        gca = gcAnalyzers.get(name + ".inc-dump");
        if (gca == null) {
            gca = new FormattedTimelineDumpGCAnalyzer();
            gcAnalyzers.put(name + ".inc-dump", gca);
        }
        gca.add(data);
    }

    protected void analyze() {
        DumpFileReader dfr = new DumpFileReader(inputs);
        dfr.read(new ObjectCallback() {

            public void process(Object object, CallbackController controller) {
                if (object instanceof ThreadData) {
                    ThreadData td = (ThreadData) object;
                    addToThreadAnalyzer("total", td);
                    addToThreadAnalyzer(td.getThreadState().toString(), td);
                } else if (object instanceof MemoryData) {
                    MemoryData md = (MemoryData) object;
                    addToMemoryAnalyzer(md);
                } else if (object instanceof GCData) {
                    GCData gc = (GCData) object;
                    addToGCAnalyzer(gc.getName(), gc);
                }
            }
        });

        for (Entry<String, AbstractThreadAnalyzer> e : threadAnalyzers.entrySet())
            e.getValue().postProcess();
        for (Entry<String, AbstractMemoryAnalyzer> e : memoryAnalyzers.entrySet())
            e.getValue().postProcess();
        for (Entry<String, AbstractGCAnalyzer> e : gcAnalyzers.entrySet())
            e.getValue().postProcess();
    }

    protected void init(Properties props) {
        String inputNamesProp = props.getProperty("input");
        if (inputNamesProp == null) {
            System.err.println("Use input=file1,file2 command line property");
            throw new RuntimeException("Input file names are not set!");
        }

        List<File> files = new ArrayList<File>();
        String[] inputNames = inputNamesProp.split("\\,");
        for (String inputName : inputNames) {
            inputName = inputName.trim();
            if (inputName.length() == 0)
                continue;

            File file = new File(inputName);
            if (!file.exists()) {
                System.err.println("File does not exists: " + inputName);
                throw new RuntimeException("File does not exists: " + inputName);
            }

            files.add(file);
        }
        this.inputs = files.toArray(new File[files.size()]);
        if (this.inputs.length == 0) {
            System.err.println("No input file. Exiting.");
            return;
        }
    }
}
