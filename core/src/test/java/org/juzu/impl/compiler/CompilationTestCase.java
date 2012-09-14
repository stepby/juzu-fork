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
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.jboss.util.NoSuchMethodException;
import org.juzu.impl.spi.fs.disk.DiskFileSystem;
import org.juzu.impl.spi.fs.ram.RAMDir;
import org.juzu.impl.spi.fs.ram.RAMFile;
import org.juzu.impl.spi.fs.ram.RAMFileSystem;
import org.juzu.impl.spi.fs.ram.RAMPath;
import org.juzu.impl.utils.Content;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 * Mar 16, 2012
 */
public class CompilationTestCase extends TestCase {
	
	public void testBar() throws Exception {
		File root = new File(System.getProperty("test.resources"));
		Compiler<File, ?> compiler = new Compiler<File, RAMPath>(new DiskFileSystem(root, "compiler", "disk"), new RAMFileSystem());
		assertEquals(Collections.emptyList(), compiler.compile());
		assertEquals(1, compiler.getClassOutputKeys().size());
	}
	
	public void testGetResourceFromProcessor() throws Exception {
		RAMFileSystem ramFS = new RAMFileSystem();
		RAMDir root = ramFS.getRoot();
		RAMDir foo = root.addDir("foo");
		foo.addFile("A.java").update("package foo; public class A {}");
		foo.addFile("A.txt").update("value");

		@javax.annotation.processing.SupportedAnnotationTypes({"*"})
		@javax.annotation.processing.SupportedSourceVersion(SourceVersion.RELEASE_6)
		class ProcessorImpl extends AbstractProcessor {

			Object result = null;
			
			@Override
			public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
				if(roundEnv.processingOver()) {
					try {
						Filer filer = processingEnv.getFiler();
						FileObject o = filer.getResource(StandardLocation.SOURCE_PATH, "foo", "A.txt");
						result = o.getCharContent(false);
					} catch(IOException e) {
						result = e;
					}
				}
				return false;
			}
		}
		
		Compiler<RAMPath, RAMPath> compiler = new Compiler<RAMPath, RAMPath>(ramFS, new RAMFileSystem());
		ProcessorImpl processor = new ProcessorImpl();
		compiler.addAnnotationProcessor(processor);
		assertEquals(Collections.emptyList(), compiler.compile());
		assertEquals(1, compiler.getClassOutputKeys().size());
		if(processor.result instanceof Exception) {
			AssertionFailedError afe = new AssertionFailedError();
			afe.initCause((Throwable)processor.result);
			throw afe;
		} else if(processor.result instanceof CharSequence) {
			assertEquals("value", processor.result);
		} else {
			fail("Was not expecting result to be " + processor.result);
		}
	}
	
	//For now we don't support the feature
	public void _testChanges() throws Exception {
		RAMFileSystem ramFS = new RAMFileSystem();
		RAMDir root = ramFS.getRoot();
		RAMDir foo = root.addDir("foo");
		RAMFile a = foo.addFile("A.java").update("package foo; public class A {}");
		RAMFile b = foo.addFile("B.java").update("package foo; public class B {}");
		
		Compiler<RAMPath, ?> compiler = new Compiler<RAMPath, RAMPath>(ramFS, new RAMFileSystem());
		assertEquals(Collections.emptyList(), compiler.compile());
		assertEquals(2, compiler.getClassOutputKeys().size());
		Content aClass = compiler.getClassOuput(FileKey.newJavaName("foo.A", JavaFileObject.Kind.CLASS));
		assertNotNull(aClass);
		Content bClass = compiler.getClassOuput(FileKey.newJavaName("foo.B", JavaFileObject.Kind.CLASS));
		assertNotNull(bClass);
		
		while(true) {
			b.update("package foo; public class B extends A {}");
			if(bClass.getLastModified() < b.getLastModified()) {
				break;
			} else {
				Thread.sleep(1);
			}
		}
		
		assertEquals(Collections.emptyList(), compiler.compile());
		assertEquals("was not expecting to be " + compiler.getClassOutputKeys(), 1, compiler.getClassOutputKeys().size());
		bClass = compiler.getClassOuput(FileKey.newJavaName("foo.A", JavaFileObject.Kind.CLASS));
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
		
		Compiler<RAMPath, ?> compiler = new Compiler<RAMPath, RAMPath>(ramFS, new RAMFileSystem());
		ProcessorImpl processor = new ProcessorImpl();
		compiler.addAnnotationProcessor(processor);
		assertEquals(Collections.emptyList(), compiler.compile());
		assertEquals(2, compiler.getClassOutputKeys().size());
		assertEquals(1, compiler.getSourceOuputKeys().size());
	}
	
	public void testCompilationFailure() throws Exception {
		RAMFileSystem fs = new RAMFileSystem();
		fs.getRoot().addFile("A.java").update("public class A {");
		Compiler<RAMPath, ?> compiler = new Compiler<RAMPath, RAMPath>(fs, new RAMFileSystem());
		assertEquals(1, compiler.compile().size());
	}
	
	public void testAnnotationException() throws Exception {
		RAMFileSystem ramFs = new RAMFileSystem();
		RAMDir root = ramFs.getRoot();
		root.addFile("A.java").update("@Deprecated public class A {}");
		
		//
		Compiler<RAMPath, ?> compiler = new Compiler<RAMPath, RAMPath>(ramFs, new RAMFileSystem());
		ProcessorPlugin plugin = new ProcessorPlugin() {
			@Override
			public void process() throws CompilationException {
				Set<? extends Element> elements = getElementsAnnotatedWith(Deprecated.class);
				if(elements.size() == 1) {
					Element element = elements.iterator().next();
					throw new CompilationException(element, "the_message");
				}
			}
		};
		compiler.addAnnotationProcessor(new Processor(plugin));
		List<CompilationError> errors = compiler.compile();
		assertEquals(1, errors.size());
		CompilationError error = errors.get(0);
		assertEquals("/A.java", error.getSource());
		assertTrue(error.getMessage().contains("the_message"));
		assertNull(error.getSourceFile());
		assertNotNull(error.getLocation());
		
		//
		compiler = new Compiler<RAMPath, RAMPath>(ramFs, new RAMFileSystem());
		plugin = new ProcessorPlugin() {
			boolean failed = false;
			@Override
			public void process() throws CompilationException {
				if(!failed) 
				{
					failed = true;
					throw new NoSuchElementException("the_message");
				}
			}
		};
		
		compiler.addAnnotationProcessor(new Processor(plugin));
		errors = compiler.compile();
		assertEquals(1, errors.size());
		error = errors.get(0);
		assertNull(error.getSource());
		assertTrue(error.getMessage().contains("the_message"));
		assertNull(error.getSourceFile());
		assertNull(error.getLocation());
	}
}
