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
package hu.oktech.profiler.analysis.sampling.thread;

import hu.oktech.profiler.analysis.tree.ItemDefault;
import hu.oktech.profiler.analysis.tree.ItemFilter;
import hu.oktech.profiler.analysis.tree.ToStringValuePrinter;
import hu.oktech.profiler.analysis.tree.Tree;
import hu.oktech.profiler.analysis.tree.TreePrinter;
import hu.oktech.profiler.runtime.sampling.thread.ThreadData;

import java.io.IOException;
import java.io.Writer;
import java.util.Comparator;
import java.util.LinkedList;

/**
 * Thread analyzer that counts the thread stackstraces in a tree format.
 * 
 * @author Istvan Soos
 */
public class TreeCounterThreadAnalyzer extends AbstractThreadAnalyzer {

	protected Tree<StatItem> counterTree = new Tree<StatItem>();
	protected boolean addThreadName = false;

	public TreeCounterThreadAnalyzer() {
		this(false);
	}

	public TreeCounterThreadAnalyzer(boolean addThreadName) {
		super();
		this.addThreadName = addThreadName;
	}

	protected ItemDefault<StatItem> counterDefaultValue = new ItemDefault<StatItem>() {

		public StatItem newDefaultValue() {
			return new StatItem();
		}
	};

	public void add(ThreadData td) {
		Tree<StatItem> item = counterTree;
		if (addThreadName) {
			item = item.getChild(td.getThreadName(), counterDefaultValue);
			item.getValue().incrementCount();
			item.getValue().markSystemTime(td.getSystemTime());
		}
		for (int i = td.getStackTrace().length - 1; i >= 0; i--) {
			StackTraceElement ste = td.getStackTrace()[i];
			String key = ste.getClassName() + "." + ste.getMethodName() + "()";
			item = item.getChild(key, counterDefaultValue);
			item.getValue().incrementCount();
			item.getValue().markSystemTime(td.getSystemTime());
		}
	}

	public Tree<StatItem> getCounterTree() {
		return counterTree;
	}

	public void print(final int minCounter, Writer out) throws IOException {
		new TreePrinter().print(counterTree, new ItemFilter<StatItem>() {

			public boolean isShown(LinkedList<Tree<StatItem>> parents, Tree<StatItem> item) {
				return item.getValue() == null || item.getValue().getCount() >= minCounter;
			}
		}, new Comparator<StatItem>() {
			public int compare(StatItem o1, StatItem o2) {
				return ((Long) o2.getCount()).compareTo(o1.getCount());
			}
		}, new ToStringValuePrinter<StatItem>(), out);
	}

	public void postProcess() {
	}

}
