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

import java.util.Collections;

import org.juzu.impl.spi.fs.ram.RAMFile;
import org.juzu.impl.spi.fs.ram.RAMFileSystem;
import org.juzu.impl.spi.fs.ram.RAMPath;
import org.juzu.test.AbstractTestCase;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public class ScannerTestCase extends AbstractTestCase {

	public void testFoo() throws Exception {
		RAMFileSystem fs = new RAMFileSystem();
		FileSystemScanner<RAMPath> scanner = new FileSystemScanner<RAMPath>(fs);
		
		//
		assertEquals(Collections.<String, Change>emptyMap(), scanner.scan());
		
		//
		RAMPath foo = fs.addDir(fs.getRoot(), "foo");
		assertEquals(Collections.<String, Change>emptyMap(), scanner.scan());
		
		RAMFile bar = fs.addFile(foo, "bar.txt");
		waitForOneMillis();
		assertEquals(Collections.singletonMap("foo/bar.txt", Change.ADD), scanner.scan());
		assertEquals(Collections.<String, Change>emptyMap(), scanner.scan());
		
		bar.update("abc");
		waitForOneMillis();
		assertEquals(Collections.singletonMap("foo/bar.txt", Change.UPDATE), scanner.scan());
		assertEquals(Collections.<String, Change>emptyMap(), scanner.scan());
		
		bar.remove();
		waitForOneMillis();
		assertEquals(Collections.singletonMap("foo/bar.txt", Change.REMOVE), scanner.scan());
		assertEquals(Collections.<String, Change>emptyMap(), scanner.scan());
	}
}
