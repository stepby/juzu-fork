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

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.juzu.impl.spi.fs.ReadFileSystem;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 * Mar 16, 2012
 */

public class DiskFileSystemTestCase extends TestCase {
	
	public void testFoo() throws IOException {
		File root = new File(System.getProperty("test.resources"));
		assertNotNull(root);
		assertTrue(root.isDirectory());
		doTest(new DiskFileSystem(root, "compiler", "disk"), root);
	}
	
	private <P> void doTest(ReadFileSystem<P> fs, P root) throws IOException {
		assertEquals(root, fs.getRoot());
		assertTrue(fs.isDir(root));
		assertFalse(fs.isFile(root));
		assertEquals("", fs.getName(root));
		assertNull(fs.getParent(root));

		Iterator<P> rootChildren = fs.getChildren(root);
		assertTrue(rootChildren.hasNext());
		P compiler = rootChildren.next();
		assertFalse(rootChildren.hasNext());
		assertTrue(fs.isDir(compiler));
		assertFalse(fs.isFile(compiler));
		assertEquals("compiler", fs.getName(compiler));
		assertEquals(root, fs.getParent(compiler));
		
		Iterator<P> orgChildren = fs.getChildren(compiler);
		assertTrue(orgChildren.hasNext());
		P disk = orgChildren.next();
		assertFalse(orgChildren.hasNext());
		assertTrue(fs.isDir(disk));
		assertFalse(fs.isFile(disk));
		assertEquals("disk", fs.getName(disk));
		assertEquals(compiler, fs.getParent(disk));
		
		Iterator<P> juzuChildren = fs.getChildren(disk);
		assertTrue(juzuChildren.hasNext());
		P a = juzuChildren.next();
		assertFalse(juzuChildren.hasNext());
		assertFalse(fs.isDir(a));
		assertTrue(fs.isFile(a));
		assertEquals("A.java", fs.getName(a));
		assertEquals(disk, fs.getParent(a));
	}
}
