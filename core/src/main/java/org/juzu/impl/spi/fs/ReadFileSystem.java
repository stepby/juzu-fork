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
package org.juzu.impl.spi.fs;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import org.juzu.impl.utils.Content;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 * Mar 16, 2012
 */
public abstract class ReadFileSystem<P> {
	
	public final void dump(Appendable appendable) throws IOException {
		dump(getRoot(), appendable);
	}
	
	public final void dump(P path, final Appendable appendable) throws IOException {
		final StringBuilder prefix = new StringBuilder();
		traverse(path, new Visitor<P>() {
			public boolean enterDir(P dir, String name) throws IOException {
				prefix.append(name).append("/");
				return true;
			}
			public void file(P file, String name) throws IOException {
				appendable.append(prefix).append(name).append("\n");
			}

			public void leaveDir(P dir, String name) throws IOException {
				prefix.setLength(prefix.length() - 1 - name.length());
			}
		});
	}
	
	public final P getFile(Iterable<String> path, String name) throws IOException {
		P dir = getDir(path);
		if(dir != null) {
			P child = getChild(dir, name);
			if(isFile(child)) return child;
		}
		return null;
	}
	
	public final P getDir(Iterable<String> path) throws IOException {
		P current = getRoot();
		for(String name : path) {
			P child = getChild(current, name);
			if(child != null && isDir(child)) {
				current = child;
			} else {
				return null;
			}
		}
		return current;
	}
	
	public final void pathOf(P path, char seperator, Appendable appendable) throws IOException {
		if(packageOf(path, seperator, appendable)) {
			appendable.append(seperator);
		}
		String name = getName(path);
		appendable.append(name);
	}
	
	public final boolean packageOf(P path, char seperator, Appendable appendable) throws NullPointerException, IOException {
		if(path == null) throw new NullPointerException("No null path accepted");
		if(appendable == null) throw new NullPointerException("No null appendable accepted");
		
		if(isDir(path)) {
			P parent = getParent(path);
			if(parent == null) {
				return false;
			} else {
				String name = getName(path);
				if(packageOf(parent, seperator, appendable)) {
					appendable.append(seperator);
				}
				appendable.append(name);
				return true;
			}
		} else {
			return packageOf(getParent(path), seperator, appendable);
		}
	}
	
	public final P getPath(Iterable<String> filePath) throws IOException {
		P current = getRoot();
		for(String name : filePath) {
			if(isDir(current)) {
				P child = getChild(current, name);
				if(child != null) {
					current = child;
				} else {
					return null;
				}
			} else {
				throw new UnsupportedOperationException("handle me gracefully");
			}
		}
		return current;
	}
	
	public final void traverse(P path, Visitor<P> visitor) throws IOException {
		String name = getName(path);
		if(isDir(path)) {
			if(visitor.enterDir(path, name)) {
				for(Iterator<P> i = getChildren(path); i.hasNext();) {
					P child = i.next();
					traverse(child, visitor);
				}
			}
		} else {
			visitor.file(path, name);
		}
	}
	
	public final void traverse(Visitor<P> visitor) throws IOException {
		traverse(getRoot(), visitor);
	}
	
	public final URL getURL() throws IOException {
		P root = getRoot();
		return getURL(root);
	}
	
	public abstract boolean equals(P left, P right);
	
	public abstract P getRoot() throws IOException;
	
	public abstract P getParent(P path) throws IOException;
	
	public abstract String getName(P path) throws IOException;
	
	public abstract Iterator<P> getChildren(P dir) throws IOException;
	
	public abstract P getChild(P dir, String name) throws IOException;
	
	public abstract boolean isDir(P path) throws IOException;
	
	public abstract boolean isFile(P path) throws IOException;
	
	public abstract Content<?> getContent(P file) throws IOException;
	
	public abstract long getLastModified(P path) throws IOException;
	
	public abstract URL getURL(P path) throws IOException;
	
	public abstract File getFile(P path) throws IOException;
}
