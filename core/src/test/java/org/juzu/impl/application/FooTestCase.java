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

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import junit.framework.TestCase;

import org.juzu.impl.compiler.CompilerContext;
import org.juzu.impl.spi.cdi.Container;
import org.juzu.impl.spi.cdi.weld.WeldContainer;
import org.juzu.impl.spi.fs.ReadWriteFileSystem;
import org.juzu.impl.spi.fs.ram.RAMDir;
import org.juzu.impl.spi.fs.ram.RAMFile;
import org.juzu.impl.spi.fs.ram.RAMFileSystem;
import org.juzu.impl.spi.fs.ram.RAMPath;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public class FooTestCase extends TestCase {

	public void testFoo() throws Exception {
		RAMFileSystem ramFS = new RAMFileSystem();
		RAMDir root = ramFS.getRoot();
		RAMDir foo = root.addDir("foo");
		RAMFile a =foo.addFile("A.java").update("package foo; @javax.inject.Named(\"bar\") public class A {" +
					"@javax.inject.Inject @org.juzu.Resource(\"my_template\") public org.juzu.template.Template template;" +
				"}");
		RAMFile b =foo.addFile("B.java").update("package foo; public class B {\n" +
				"@javax.enterprise.inject.Produces\n" +
				"public org.juzu.template.Template getTemplate(javax.enterprise.inject.spi.InjectionPoint injection) {\n" +
				"javax.enterprise.inject.spi.Annotated annotated = injection.getAnnotated();\n" +
				"org.juzu.Resource template = annotated.getAnnotation(org.juzu.Resource.class);\n" +
				"return new org.juzu.template.Template(template.value());\n" +
				"}\n" +
				"}");
		foo.addDir("META-INF").addFile("beans.xml").update("<beans/>");

		//
		long now = System.currentTimeMillis();
		final ReadWriteFileSystem<RAMPath> output = new RAMFileSystem();
		final CompilerContext<?, ?> compiler = new CompilerContext<RAMPath, RAMPath>(ramFS, output);
		assertTrue(compiler.compile());
		now = System.currentTimeMillis() - now;
		System.out.println("Compiled files in " + now + " ms");

		//
		ClassLoader cl = new URLClassLoader(new URL[] { output.getURL() }, Thread.currentThread().getContextClassLoader());
		
		//
		now = System.currentTimeMillis();
		Container container = new WeldContainer(cl);
		container.addFileSystem(output);
		container.start();
		now = System.currentTimeMillis() - now;
		System.out.println("Started CDI in " + now + " ms");
		
		//
		BeanManager mgr = container.getManager();
		assertNotNull(mgr);
		Set<? extends Bean> beans = mgr.getBeans("bar");
		assertEquals(1, beans.size());
		Bean bean = beans.iterator().next();
		CreationalContext<?> cc = mgr.createCreationalContext(bean);
		Class aClass = container.getClassLoader().loadClass("foo.A");
		Object o = mgr.getReference(bean, aClass, cc);
		System.out.println("o = " + o);
		Object renderer = aClass.getField("template").get(o);
		System.out.println("renderer = " + renderer);
	}
}
