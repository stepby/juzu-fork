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
import java.net.URL;
import java.util.Iterator;

import org.juzu.impl.spi.fs.ReadWriteFileSystem;
import org.juzu.impl.utils.Content;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 * Mar 16, 2012
 */
public class RAMFileSystem extends ReadWriteFileSystem<RAMPath> {
	
	private final RAMDir root;
	
	private final URL contextURL;
	
	public RAMFileSystem() throws IOException {
		this.root = new RAMDir();
		this.contextURL = new URL("juzu", null, 0, "/", new RAMURLStreamHandler(this));
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
	
	public URL getURL(RAMPath path) throws IOException {
		StringBuilder sb = new StringBuilder();
		pathOf(path, '/', sb);
		String spec = sb.toString();
		return new URL(contextURL, spec);
	}

	@Override
	public RAMPath addDir(RAMPath parent, String name) throws IOException {
		return ((RAMDir) parent).addDir(name);
	}

	@Override
	public RAMPath addFile(RAMPath parent, String name) throws IOException {
		return ((RAMDir) parent).addFile(name);
	}

	@Override
	public void setContent(RAMPath file, Content<?> content) throws IOException {
		((RAMFile) file).update(content);
	}
}
