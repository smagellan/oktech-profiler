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
package hu.oktech.profiler.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Utility class that can help to parse and process the String properties and
 * create {@link Properties} object.
 * 
 * @author Istvan Soos
 * 
 */
public abstract class PropertyUtils {

	public static Properties parse(String properties, String separator) {
		Properties props = new Properties();
		String[] parts = properties.split(separator);
		for (String part : parts) {
			String[] kv = part.split("=", 2);
			if (kv.length != 2)
				continue;
			if ("prop".equals(kv[0])) {
				props.putAll(load(kv[1]));
			} else {
				props.put(kv[0], kv[1]);
			}
		}

		return props;
	}

	public static Properties load(String path) {
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(path));
		} catch (FileNotFoundException e) {
			new RuntimeException("Error loading properties: " + path, e);
		} catch (IOException e) {
			new RuntimeException("Error loading properties: " + path, e);
		}
		return props;
	}

	public static Properties parse(String[] args) {
		Properties props = new Properties();
		for (String arg : args) {
			String[] s = arg.split("=", 2);
			if (s.length == 2) {
				if ("prop".equals(s[0])) {
					props.putAll(load(s[1]));
				} else {
					props.setProperty(s[0], s[1]);
				}
			}
		}
		return props;
	}
}
