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

import java.io.IOException;
import java.util.Iterator;

import org.juzu.impl.spi.fs.FileSystem;
import org.juzu.impl.utils.Content;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 * Mar 16, 2012
 */
public class RAMFileSystem extends FileSystem<RAMPath> {
	
	private final RAMDir root;
	
	public RAMFileSystem() {
		this.root = new RAMDir();
	}

	public boolean equals(RAMPath left, RAMPath right) {
		return left == right;
	}

	public RAMDir getRoot() throws IOException {
		return root;
	}

	public RAMDir getParent(RAMPath path) throws IOException {
		return path.getParent();
	}

	public String getName(RAMPath path) throws IOException {
		return path.getName();
	}

	public Iterator<RAMPath> getChildren(RAMPath dir) throws IOException {
		return ((RAMDir)dir).children.values().iterator();
	}
	
	public RAMPath getChild(RAMPath dir, String name) throws IOException {
		return ((RAMDir)dir).children.get(name);
	}

	public boolean isDir(RAMPath path) throws IOException {
		return path instanceof RAMDir;
	}

	public boolean isFile(RAMPath path) throws IOException {
		return path instanceof RAMFile;
	}

	public Content<?> getContent(RAMPath file) throws IOException {
		return ((RAMFile)file).getContent();
	}

	public long getLastModified(RAMPath path) throws IOException {
		return path.getLastModified();
	}
}
