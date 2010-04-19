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
package hu.oktech.profiler.analysis.tree;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * A utility class that can be used to print a tree to a {@link Writer}
 * 
 * @author Istvan Soos
 */
public class TreePrinter {

	public <T> void print(Tree<T> root, ItemFilter<T> filter, Comparator<T> comparator,
	        ValuePrinter<T> printer) {
		try {
			print(root, filter, comparator, printer, new OutputStreamWriter(System.out));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public <T> void print(Tree<T> root, ItemFilter<T> filter, Comparator<T> comparator,
	        ValuePrinter<T> printer, Writer out) throws IOException {
		print("", 0, new LinkedList<Tree<T>>(), root, filter, comparator, printer, out);
	}

	public <T> void print(String prefix, int depth, LinkedList<Tree<T>> parents, Tree<T> item,
	        ItemFilter<T> filter, Comparator<T> comparator, ValuePrinter<T> printer, Writer out)
	        throws IOException {
		if (filter != null && !filter.isShown(parents, item))
			return;

		String value = printer.print(parents, item);
		if (prefix.length() > 0) {
			out.write(prefix.substring(0, prefix.length() - 1));
			out.write("|");
		}
		if (value != null)
			out.write(value);
		out.write("\n");
		out.flush();

		List<Tree<T>> children = item.listChildren(comparator);
		if (children != null) {
			for (int i = 0; i < children.size(); i++) {
				Tree<T> child = children.get(i);
				String np = i < children.size() - 1 ? prefix + "  |" : prefix + "   ";

				parents.add(item);
				print(np, depth + 1, parents, child, filter, comparator, printer, out);
				parents.removeLast();
			}
		}
	}
}
