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

import org.juzu.impl.spi.fs.FileSystem;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 * Mar 16, 2012
 */

public class DiskFileSystemTestCase extends TestCase {
	
	public void testFoo() throws IOException {
		File file = new File(System.getProperty("test.resources"));
		assertNotNull(file);
		assertTrue(file.isDirectory());
		doTest(new DiskFileSystem(file), file);
	}
	
	private <P, D extends P, F extends P> void doTest(FileSystem<P, D, F> fs, D root) throws IOException {
		assertEquals(root, fs.getRoot());
		
		assertTrue(fs.isDir(root));
		assertFalse(fs.isFile(root));
		assertEquals("", fs.getName(root));
		assertNull(fs.getParent(root));

		Iterator<P> rootChildren = fs.getChildren(root);
		assertTrue(rootChildren.hasNext());
		P org = rootChildren.next();
		assertFalse(rootChildren.hasNext());
		assertTrue(fs.isDir(org));
		assertFalse(fs.isFile(org));
		assertEquals("org", fs.getName(org));
		assertEquals(root, fs.getParent(org));
		
		Iterator<P> orgChildren = fs.getChildren(fs.asDir(org));
		assertTrue(orgChildren.hasNext());
		P juzu = orgChildren.next();
		assertFalse(orgChildren.hasNext());
		assertTrue(fs.isDir(juzu));
		assertFalse(fs.isFile(juzu));
		assertEquals("juzu", fs.getName(juzu));
		assertEquals(org, fs.getParent(juzu));
		
		Iterator<P> juzuChildren = fs.getChildren(fs.asDir(juzu));
		assertTrue(juzuChildren.hasNext());
		P a = juzuChildren.next();
		assertFalse(juzuChildren.hasNext());
		assertFalse(fs.isDir(a));
		assertTrue(fs.isFile(a));
		assertEquals("A.java", fs.getName(a));
		assertEquals(juzu, fs.getParent(a));
	}
}
