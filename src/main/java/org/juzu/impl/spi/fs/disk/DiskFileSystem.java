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
package org.juzu.impl.spi.fs.disk;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import org.juzu.impl.spi.fs.FileSystem;
import org.juzu.impl.utils.Content;
import org.juzu.impl.utils.Safe;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 * Mar 16, 2012
 */
public class DiskFileSystem implements FileSystem<File, File, File> {
	
	private final File root;
	
	public DiskFileSystem(File root) {
		this.root = root;
	}

	public boolean equals(File left, File right) {
		return left.equals(right);
	}

	public File getRoot() throws IOException {
		return root;
	}

	public File getParent(File path) throws IOException {
		if(path.equals(root)) return null;
		else return path.getParentFile();
	}

	public String getName(File path) throws IOException {
		if(path.equals(root)) return "";
		else return path.getName();
	}

	public Iterator<File> getChildren(File dir) throws IOException {
		return Arrays.asList(dir.listFiles()).iterator();
	}

	public boolean isDir(File path) throws IOException {
		return path.isDirectory();
	}

	public boolean isFile(File path) throws IOException {
		return path.isFile();
	}

	public File asFile(File path) throws IllegalArgumentException, IOException {
		if(!path.isFile()) throw new IllegalArgumentException("File " + path + " is not a file");
		return path;
	}

	public File asDir(File path) throws IllegalArgumentException, IOException {
		if(!path.isDirectory()) throw new IllegalArgumentException("File " + path + " is not a directory");
		return path;
	}
	
	public File getChild(File dir, String name) throws IOException {
		File child = new File(dir, name);
		return child.exists() ? child : null;
	}

	public Content<?> getContent(File file) throws IOException {
		FileInputStream	in = new FileInputStream(file);
		try {
			ByteArrayOutputStream content = new ByteArrayOutputStream();
			byte[] bytes = new byte[256];
			for(int l = in.read(bytes); l != -1; l = in.read(bytes)) {
				content.write(bytes, 0, l);
			}
			return new Content.ByteArray(file.lastModified(), content.toByteArray());
		} finally {
			Safe.close(in);
		}
	}

	public long getLastModified(File path) throws IOException {
		return path.lastModified();
	}
}
