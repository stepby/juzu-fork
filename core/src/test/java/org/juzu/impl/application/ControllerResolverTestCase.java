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
import org.juzu.impl.utils.Builder;
import org.juzu.test.AbstractTestCase;
import org.juzu.test.CompilerHelper;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public class ControllerResolverTestCase extends AbstractTestCase {

	public void testResolution() throws Exception {
		final File root = new File("src/test/resources");
		DiskFileSystem fs = new DiskFileSystem(root, "controller_resolver");
		//
		CompilerHelper<File> compiler = new CompilerHelper<File>(fs);
		compiler.assertCompile();
		
		Class<?> clazz = compiler.assertClass("controller_resolver.Controller_resolverApplication");
		ApplicationDescriptor desc = (ApplicationDescriptor)clazz.getField("DESCRIPTOR").get(null);
		ControllerResolver resolver = new ControllerResolver(desc);
		
		ControllerMethod cm1 = resolver.resolve(Phase.RENDER, Collections.<String, String[]>emptyMap());
		assertNotNull(cm1);
		assertEquals("noArg", cm1.getMethodName());
		
		ControllerMethod cm2 = resolver.resolve(Phase.RENDER, Collections.singletonMap("foo", new String[] { "value" }));
		assertNotNull(cm2);
		assertEquals("fooArg", cm2.getMethodName());
		
		ControllerMethod cm3 = resolver.resolve(Phase.RENDER, Collections.singletonMap("foo", new String[] { "foo_value" }));
		assertNotNull(cm3);
		assertEquals("fooBinding", cm3.getMethodName());
		
		try {
			resolver.resolve(Phase.RENDER, Builder.map("foo", new String[] { "foo_value" }).put("bar", new String[] {"bar_value"}).build());
			fail();
		} catch(AmbiguousResolutionException ignore) {
		}
	}
}
