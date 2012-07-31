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
package org.juzu.impl.utils;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ExplodedExporter;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.util.file.JarArchiveBrowser;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public class DevClassLoaderTestCase extends TestCase {

	private File targetDir;
	
	@Override
	protected void setUp() throws Exception {
		String targetPath = System.getProperty("targetDir");
		assertNotNull(targetPath);
		File targetDir = new File(targetPath);
		assertTrue(targetDir.isDirectory());
		this.targetDir = targetDir;
	}
	
	private ClassLoader getParentClassLoader() throws Exception {
		ClassLoader systemCL = ClassLoader.getSystemClassLoader();
		ClassLoader extCL = systemCL.getParent();
		try {
			extCL.loadClass(Dev.class.getName());
			fail();
		} catch(ClassNotFoundException e) {
		}
		
		try {
			extCL.loadClass(Lib.class.getName());
			fail();
		} catch(ClassNotFoundException e) {
		}
		return extCL;
	}
	
	public void testExploded() throws Exception {
		WebArchive archive = ShrinkWrap.create(WebArchive.class, "exploded.war").addClass(Dev.class).addDirectories("WEB-INF/lib");
		File explodedDir = archive.as(ExplodedExporter.class).exportExploded(targetDir);
		File libJar = new File(explodedDir, "WEB-INF/lib/lib.jar");
		ShrinkWrap.create(JavaArchive.class).addClass(Lib.class).as(ZipExporter.class).exportZip(libJar);
		
		//
		File classesDir = new File(explodedDir, "WEB-INF/classes");
		assertTrue(classesDir.isDirectory());
		
		//Build a correct parent CL
		URLClassLoader cl = new URLClassLoader(new URL[] { classesDir.toURI().toURL(), libJar.toURI().toURL() }, getParentClassLoader());
		Class<?> devClass = cl.loadClass(Dev.class.getName());
		assertNotSame(devClass, Dev.class);
		Class<?> libClass = cl.loadClass(Lib.class.getName());
		assertNotSame(libClass, Lib.class);
		
		DevClassLoader devCL = new DevClassLoader(cl);
		try {
			devCL.loadClass(Dev.class.getName());
			fail();
		} catch(ClassNotFoundException e) {
		}
		assertSame(libClass, devCL.loadClass(Lib.class.getName()));
	}
}
