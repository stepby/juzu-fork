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
package org.juzu.impl.template;

import java.io.StringWriter;
import java.net.URL;
import java.net.URLClassLoader;

import javax.tools.JavaFileObject;

import junit.framework.TestCase;

import org.juzu.impl.compiler.CompilerContext;
import org.juzu.impl.compiler.FileKey;
import org.juzu.impl.spi.fs.ram.RAMDir;
import org.juzu.impl.spi.fs.ram.RAMFile;
import org.juzu.impl.spi.fs.ram.RAMFileSystem;
import org.juzu.impl.spi.fs.ram.RAMPath;
import org.juzu.impl.spi.template.TemplateStub;
import org.juzu.impl.utils.Content;
import org.juzu.text.WriterPrinter;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public class TemplateProcessorTestCase extends TestCase {

	public void testFoo() throws Exception {
		RAMFileSystem ramFS = new RAMFileSystem();
		RAMDir root = ramFS.getRoot();
		RAMDir foo = root.addDir("foo");
		RAMFile a = foo.addFile("A.java").update("package foo; public class A { @org.juzu.template.Template(\"B.gtmpl\") org.juzu.template.TemplateRenderer template; }");
		RAMFile b = foo.addFile("B.gtmpl").update("<% out.print('hello') %>");
		RAMFileSystem output = new RAMFileSystem();
		final CompilerContext<RAMPath, ?> compiler = new CompilerContext<RAMPath, RAMPath>(ramFS, output);
		compiler.addAnnotationProcessor(new TemplateProcessor());
		assertTrue(compiler.compile());
		
		//
		Content content = compiler.getClassOuput(FileKey.newResourceName("foo", "B.groovy"));
		assertNotNull(content);
		assertEquals(3, compiler.getClassOutputKeys().size());
		
		//
		assertEquals(1, compiler.getSourceOuputKeys().size());
		Content content2 = compiler.getSourceOutput(FileKey.newJavaName("foo.B", JavaFileObject.Kind.SOURCE));
		assertNotNull(content2);
		
		ClassLoader cl = new URLClassLoader(new URL[] { output.getURL() }, Thread.currentThread().getContextClassLoader());
		
		Class<?> aClass = cl.loadClass("foo.A");
		Class<?> bClass = cl.loadClass("foo.B");
		TemplateStub template = (TemplateStub) bClass.newInstance();
		StringWriter writer = new StringWriter();
		template.render(new WriterPrinter(writer), null, null);
		assertEquals("hello", writer.toString());
	}
}
