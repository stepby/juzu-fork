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
package org.juzu.impl.request;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import junit.framework.TestCase;

import org.juzu.impl.spi.fs.disk.DiskFileSystem;
import org.juzu.impl.spi.fs.ram.RAMPath;
import org.juzu.test.CompilerHelper;
import org.juzu.test.Registry;
import org.juzu.test.support.Car;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public class ScopeTestCase extends TestCase
{
	public void testRenderScope() throws Exception {
		if(System.getProperty("test.resources") == null) System.setProperty("test.resources", "src/test/resources");
		final File root = new File(System.getProperty("test.resources"));
		DiskFileSystem fs = new DiskFileSystem(root, "request", "scope", "render");
		
		CompilerHelper<File> compiler = new CompilerHelper<File>(fs);
		compiler.assertCompile();
		ClassLoader cl2 = new URLClassLoader(new URL[] { compiler.getOutput().getURL() }, Thread.currentThread().getContextClassLoader());
		MockApplication<RAMPath> app = new MockApplication<RAMPath>(compiler.getOutput(), cl2);
		app.init();
		
		MockClient client = app.client();
		MockRenderBridge render = client.render();
		assertEquals(1, render.getAttributes().size());
		long identity = Registry.<Long>unset("car");
		Car car = (Car) render.getAttributes().values().iterator().next();
		assertEquals(identity, car.getIdentityHashCode());
		
		client.invoke(Registry.<String>unset("action"));
		assertNull(Registry.get("car"));
		
		client.invoke(Registry.<String>unset("resource"));
		assertNull(Registry.get("resource"));
	}
	
	public void testFlashScope() throws Exception {
		if(System.getProperty("test.resources") == null) System.setProperty("test.resources", "src/test/resources");
		final File root = new File(System.getProperty("test.resources"));
		DiskFileSystem fs = new DiskFileSystem(root, "request", "scope", "flash");
		
		CompilerHelper<File> compiler = new CompilerHelper<File>(fs);
		compiler.assertCompile();
		ClassLoader cl2 = new URLClassLoader(new URL[] { compiler.getOutput().getURL() }, Thread.currentThread().getContextClassLoader());
		MockApplication<RAMPath> app = new MockApplication<RAMPath>(compiler.getOutput(), cl2);
		app.init();
		
		MockClient client = app.client();
		MockRenderBridge render = client.render();
		long identity1 = Registry.<Long>unset("car");
		assertEquals(1, client.getFlash(1).size());
		Car car1 = (Car)client.getFlash(1).values().iterator().next();
		assertEquals(car1.getIdentityHashCode(), identity1);
		
		client.invoke(Registry.<String>unset("action"));
		long identity2 = Registry.<Long>unset("car");
		assertNotSame(identity1, identity2);
		assertEquals(1, client.getFlash(0).size());	
		Car car2 = (Car)client.getFlash(0).values().iterator().next();
		assertNotSame(car1, car2);
		
		client.render();
		long identity3 = Registry.<Long>unset("car");
		assertEquals(identity2, identity3);
		assertEquals(1, client.getFlash(1).size());
		Car car3 = (Car)client.getFlash(1).values().iterator().next();
		assertSame(car2, car3);
	}
}
