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
package org.juzu.impl.spi.fs.ram;

import java.util.LinkedHashMap;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 * Mar 16, 2012
 */
public class RAMDir extends RAMPath {

	final LinkedHashMap<String, RAMPath> children;
	
	private long lastModified;
	
	private String content;
	
	public RAMDir() {
		this.children = new LinkedHashMap<String, RAMPath>();
		this.lastModified = System.currentTimeMillis();
	}
	
	public RAMDir(RAMDir parent, String name) {
		super(parent, name);
		this.children = new LinkedHashMap<String, RAMPath>();
	}
	
	public RAMFile addFile(String name) {
		if(name == null) throw new NullPointerException();
		if(name.indexOf('/') != -1) throw new IllegalArgumentException("Name must not containe '/'");
		if(children.containsKey(name)) throw new IllegalArgumentException();
		
		RAMFile file = new RAMFile(this, name);
		children.put(name, file);
		return file;
	}
	
	public RAMDir addDir(String name) {
		if(name == null) throw new NullPointerException();
		if(name.indexOf('/') != -1) throw new IllegalArgumentException("Name must not containe '/'");
		if(children.containsKey(name)) throw new IllegalArgumentException();
		
		RAMDir dir = new RAMDir(this, name);
		children.put(name, dir);
		return dir;
	}
	
	public RAMPath getChild(String name) {
		return 	children.get(name);
	}
	
	public Iterable<RAMPath> getChildren() {
		return children.values();
	}

	@Override
	public long getLastModified() {
		return lastModified;
	}

	@Override
	public void touch() {
		lastModified = System.currentTimeMillis();
	}
}
