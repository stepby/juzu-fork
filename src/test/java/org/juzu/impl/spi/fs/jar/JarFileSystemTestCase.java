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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.jar.JarFile;

import org.juzu.impl.spi.fs.Visitor;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public class JarFileSystemTestCase extends TestCase {

	public void testFoo() throws Exception {
		URL url = TestCase.class.getProtectionDomain().getCodeSource().getLocation();
		System.out.println(url);
		JarFile file = new JarFile(new File(url.toURI()));
		final JarFileSystem fs = new JarFileSystem(file);
		fs.traverse(new Visitor.Default<JarPath>() {
			@Override
			public boolean enterDir(JarPath dir, String name) throws IOException {
				StringBuilder sb = new StringBuilder();
				fs.packageOf(dir, '/', sb);
				System.out.println("dir " + sb);
				return true;
			}
		});
	}
}
