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
import java.util.Enumeration;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.juzu.impl.spi.fs.ReadFileSystem;
import org.juzu.impl.utils.Content;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public class JarFileSystem extends ReadFileSystem<JarPath> {
	
	private final JarFile jar;
	
	private final JarPath root;
	
	public JarFileSystem(JarFile jar) {
		JarPath root = new JarPath();
		for(Enumeration<JarEntry> en = jar.entries(); en.hasMoreElements();) {
			JarEntry entry = en.nextElement();
			root.append(entry);
		}
		
		//
		this.root = root;
		this.jar =jar;
	}

	@Override
	public boolean equals(JarPath left, JarPath right) {
		return left == right;
	}

	@Override
	public JarPath getRoot() throws IOException {
		return root;
	}

	@Override
	public JarPath getParent(JarPath path) throws IOException {
		return path.parent;
	}

	@Override
	public String getName(JarPath path) throws IOException {
		return path.name;
	}

	@Override
	public Iterator<JarPath> getChildren(JarPath dir) throws IOException {
		if(isFile(dir)) throw new IllegalArgumentException("Not a directory");
		return dir.getChildren();
	}

	@Override
	public JarPath getChild(JarPath dir, String name) throws IOException {
		if(isFile(dir)) throw new IllegalArgumentException("Not a directory");
		return dir.getChild(name);
	}

	@Override
	public boolean isDir(JarPath path) throws IOException {
		return path.dir;
	}

	@Override
	public boolean isFile(JarPath path) throws IOException {
		return !isDir(path);
	}

	@Override
	public Content<?> getContent(JarPath file) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getLastModified(JarPath path) throws IOException {
		return 0;
	}
}
