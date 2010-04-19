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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The tree abstraction that can be used to build and traverse hierarchical
 * models.
 * 
 * @author Istvan Soos
 * 
 * @param <T>
 *            the generic type of the tree value
 */
public class Tree<T> {

	protected String key;

	protected Map<String, Tree<T>> children;

	protected T value;

	public Tree() {
		super();
	}

	public Tree(String key, T value) {
		super();
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Map<String, Tree<T>> getChildren() {
		return children;
	}

	public void setChildren(Map<String, Tree<T>> children) {
		this.children = children;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public Tree<T> getChild(String key, ItemDefault<T> defaultValue) {
		if (children == null) {
			if (defaultValue != null) {
				children = new HashMap<String, Tree<T>>();
				Tree<T> child = new Tree<T>(key, defaultValue.newDefaultValue());
				children.put(key, child);
				return child;
			} else {
				return null;
			}
		}

		Tree<T> child = children.get(key);
		if (child != null)
			return child;
		if (defaultValue != null) {
			child = new Tree<T>(key, defaultValue.newDefaultValue());
			children.put(key, child);
			return child;
		}
		return null;
	}

	public void merge(Tree<T> addition, ValueMerger<T> merger) {
		this.value = merger.merge(value, addition.value);

		if (children != null) {
			if (addition.children != null) {
				for (Map.Entry<String, Tree<T>> entry : children.entrySet()) {
					Tree<T> ac = addition.children.get(entry.getKey());
					if (ac != null)
						entry.getValue().merge(ac, merger);
				}
			}
		}
		if (addition.children != null) {
			if (this.children == null)
				this.children = new HashMap<String, Tree<T>>();
			for (Map.Entry<String, Tree<T>> entry : addition.children.entrySet()) {
				if (!this.children.containsKey(entry.getKey()))
					this.children.put(entry.getKey(), entry.getValue());
			}
		}
	}

	public List<Tree<T>> listChildren(final Comparator<T> comparator) {
		if (this.children == null)
			return null;
		List<Tree<T>> result = new ArrayList<Tree<T>>(children.values());
		Collections.sort(result, new Comparator<Tree<T>>() {

			public int compare(Tree<T> o1, Tree<T> o2) {
				return comparator.compare(o1.getValue(), o2.getValue());
			}
		});
		return result;
	}
}
