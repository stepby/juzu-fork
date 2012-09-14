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
package org.juzu.impl.application;

import java.io.File;
import java.util.Collections;

import org.juzu.AmbiguousResolutionException;
import org.juzu.application.ApplicationDescriptor;
import org.juzu.application.Phase;
import org.juzu.impl.request.ControllerMethod;
import org.juzu.impl.spi.fs.disk.DiskFileSystem;
import org.juzu.test.AbstractTestCase;
import org.juzu.test.CompilerHelper;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public class ControllerTestCase extends AbstractTestCase
{
	public void testDefaultController() throws Exception {
		final File root = new File(System.getProperty("test.resources"));
		DiskFileSystem fs = new DiskFileSystem(root, "application", "default_controller");

		//
		CompilerHelper<File> compiler = new CompilerHelper<File>(fs);
		compiler.assertCompile();
		Class<?> appClass =compiler.assertClass("application.default_controller.Default_controllerApplication");
		Class<?> aClass = compiler.assertClass("application.default_controller.A");
		
		//
		ApplicationDescriptor desc = (ApplicationDescriptor)appClass.getDeclaredField("DESCRIPTOR").get(null);
		assertSame(aClass, desc.getDefaultController());
	}
	
	public void testResolverDefaultMethod() throws Exception {
		final File root = new File(System.getProperty("test.resources"));
		DiskFileSystem fs = new DiskFileSystem(root, "application", "resolver", "default_method");

		//
		CompilerHelper<File> compiler = new CompilerHelper<File>(fs);
		compiler.assertCompile();
		Class<?> appClass =compiler.assertClass("application.resolver.default_method.Default_methodApplication");
		
		//
		ApplicationDescriptor desc = (ApplicationDescriptor)appClass.getDeclaredField("DESCRIPTOR").get(null);
		ControllerResolver resolver = new ControllerResolver(desc);
		ControllerMethod method = resolver.resolve(Phase.RENDER, Collections.<String, String[]>emptyMap());
		assertEquals("index", method.getName());
	}
	
	public void testResolverAmbiguousMethod() throws Exception {
		final File root = new File(System.getProperty("test.resources"));
		DiskFileSystem fs = new DiskFileSystem(root, "application", "resolver", "ambiguous_method");

		//
		CompilerHelper<File> compiler = new CompilerHelper<File>(fs);
		compiler.assertCompile();
		Class<?> appClass =compiler.assertClass("application.resolver.ambiguous_method.Ambiguous_methodApplication");
		
		//
		ApplicationDescriptor desc = (ApplicationDescriptor)appClass.getDeclaredField("DESCRIPTOR").get(null);
		ControllerResolver resolver = new ControllerResolver(desc);
		try {
			resolver.resolve(Phase.RENDER, Collections.<String, String[]>emptyMap());
			fail();
		} catch(AmbiguousResolutionException e) {
		}
	}
	
	public void testResolverDefaultController() throws Exception {
		final File root = new File(System.getProperty("test.resources"));
		DiskFileSystem fs = new DiskFileSystem(root, "application", "resolver", "default_controller");

		//
		CompilerHelper<File> compiler = new CompilerHelper<File>(fs);
		compiler.assertCompile();
		Class<?> appClass =compiler.assertClass("application.resolver.default_controller.Default_controllerApplication");
		Class<?> aClass = compiler.assertClass("application.resolver.default_controller.A");
		
		//
		ApplicationDescriptor desc = (ApplicationDescriptor)appClass.getDeclaredField("DESCRIPTOR").get(null);
		ControllerResolver resolver = new ControllerResolver(desc);
		ControllerMethod method = resolver.resolve(Phase.RENDER, Collections.<String, String[]>emptyMap());
		assertEquals("index", method.getName());
		assertSame(aClass, method.getMethod().getDeclaringClass());
	}
}
