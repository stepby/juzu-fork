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
package org.juzu.impl.compiler;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

import org.juzu.impl.spi.fs.disk.DiskFileSystem;
import org.juzu.impl.spi.fs.ram.RAMDir;
import org.juzu.impl.spi.fs.ram.RAMFile;
import org.juzu.impl.spi.fs.ram.RAMFileSystem;
import org.juzu.impl.spi.fs.ram.RAMPath;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 * Mar 16, 2012
 */
public class CompilationTestCase extends TestCase {
	
	public void testBar() throws Exception {
		File root = new File(System.getProperty("test.resources"));
		CompilerContext<File, File, File> ctx = new CompilerContext<File, File, File>(new DiskFileSystem(root));
		Map<String, ClassFile> res = ctx.compile();
		assertEquals(1, res.size());
	}
	
	public void testChanges() throws Exception {
		RAMFileSystem ramFS = new RAMFileSystem();
		RAMDir root = ramFS.getRoot();
		RAMDir foo = root.addDir("foo");
		RAMFile a = foo.addFile("A.java").update("package foo; public class A {}");
		RAMFile b = foo.addFile("B.java").update("package foo; public class B {}");
		
		CompilerContext<RAMPath, RAMDir, RAMFile> ctx = new CompilerContext<RAMPath, RAMDir, RAMFile>(ramFS);
		Map<String, ClassFile> res = ctx.compile();
		assertEquals(2, res.size());
		ClassFile aClass = res.get("foo.A");
		assertNotNull(aClass);
		ClassFile bClass = res.get("foo.B");
		assertNotNull(bClass);
		
		while(true) {
			b.update("package foo; public class B extends A {}");
			if(bClass.getLastModified() < b.getLastModified()) {
				break;
			} else {
				Thread.sleep(1);
			}
		}
		
		res = ctx.compile();
		assertEquals(1, res.size());
		bClass = res.get("foo.B");
		assertNotNull(bClass);
	}

	@SupportedAnnotationTypes({"*"})
	@SupportedSourceVersion(SourceVersion.RELEASE_6)
	public static class ProcessorImpl extends AbstractProcessor {
		
		final List<String> names = new ArrayList<String>();
		
		private boolean done;

		@Override
		public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
			for(Element elt : roundEnv.getRootElements()) {
				if(elt instanceof TypeElement) {
					TypeElement typeElt = (TypeElement)elt;
					names.add(typeElt.getQualifiedName().toString());
				}
			}
			
			//
			if(!done) {
				try {
					Filer filer = processingEnv.getFiler();
					JavaFileObject b = filer.createSourceFile("B");
					PrintWriter writer = new PrintWriter(b.openWriter());
					writer.println("public class B { }");
					writer.close();
					done = true;
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			//
			return true;
		}
	}
	
	public void testProcessor() throws Exception {
		RAMFileSystem ramFS = new RAMFileSystem();
		RAMDir root = ramFS.getRoot();
		RAMFile a = root.addFile("A.java").update("public class A { }");
		
		CompilerContext<RAMPath, RAMDir, RAMFile> compiler = new CompilerContext<RAMPath, RAMDir, RAMFile>(ramFS);
		ProcessorImpl processor = new ProcessorImpl();
		compiler.addAnnotationProcessor(processor);
		Map<String, ClassFile> res = compiler.compile();
		assertNotNull(res);
		assertEquals(2, res.size());
		assertEquals(Arrays.asList("A", "B"), processor.names);
	}
}
