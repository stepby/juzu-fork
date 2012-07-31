/*
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.juzu.impl.spi.fs.jar;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.jar.JarEntry;

import org.juzu.impl.utils.Spliterator;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public class JarPath {
	
	final JarFileSystem owner;

	final JarPath parent;
	
	final String entryName;
	
	final String name;
	
	URL url;
	
	final boolean dir;
	
	private JarEntry entry;
	
	private LinkedHashMap<String, JarPath> children;
	
	public JarPath(JarFileSystem owner) {
		this.entryName = "";
		this.name = "";
		this.dir = true;
		this.entry = null;
		this.children = null;
		this.parent = null;
		this.owner = owner;
	}
	
	public JarPath(JarFileSystem owner, JarPath parent, String entryName, String name, boolean dir) {
		this.parent = parent;
		this.entryName = entryName;
		this.name = name;
		this.dir = dir;
		this.children = null;
		this.owner = owner;
	}
	
	Iterator<JarPath> getChildren() {
		if(children == null || children.isEmpty()) {
			return Collections.<JarPath>emptyList().iterator();
		}
		return children.values().iterator();
	}
	
	JarPath getChild(String name) {
		if(children == null || children.isEmpty()) return null;
		return children.get(name);
	}
	
	URL getURL() throws IOException {
		if(url == null) {
			url = new URL("jar:" + owner.jarURL + "!/" + entryName);
		}
		return url;
	}
	
	void append(JarEntry entry) {
		String entryName = entry.getName();
		boolean dir = entryName.charAt(entryName.length() - 1) == '/';
		String path = entryName.substring(0, entryName.length() - (dir ? 1 : 0));
		Iterator<String> names = new Spliterator(path, '/');
		JarPath current = this;

		StringBuilder sb = new StringBuilder();
		while(true) {
			String name = names.next();
			sb.append(name);
			
			if(current.children == null) {
				current.children = new LinkedHashMap<String, JarPath>();
			}
			
			JarPath existing = current.children.get(name);
			if(names.hasNext()) {
				sb.append('/');
				if(existing == null) {
					current.children.put(name, existing = new JarPath(owner, current, sb.toString(), name, true));
				}
				current = existing;
			} else {
				if(existing == null) {
					if(dir) {
						sb.append('/');
					}
					current.children.put(name, existing = new JarPath(owner, current, sb.toString(), name, dir)); 
					existing.entry = entry;
				} else {
					if(dir != existing.dir) {
						throw new AssertionError();
					}
					if(existing.entry != null) {
						throw new AssertionError();
					}
					existing.entry = entry;
				}
				break;
			}
		}
	}
}
