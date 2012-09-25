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
package org.juzu.test;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.List;

import org.juzu.application.JuzuProcessor;
import org.juzu.impl.compiler.CompilationError;
import org.juzu.impl.compiler.Compiler;
import org.juzu.impl.spi.fs.ReadFileSystem;
import org.juzu.impl.spi.fs.ram.RAMFileSystem;
import org.juzu.impl.spi.fs.ram.RAMPath;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public class CompilerHelper<I> {

	private ReadFileSystem<I> input;
	
	private RAMFileSystem output;
	
	private ClassLoader cl;
	
	private Compiler<I, RAMPath> compiler;
	
	public CompilerHelper(ReadFileSystem<I> in) {
		try {
			this.input = in;
			this.output = new RAMFileSystem();
			compiler = new Compiler<I, RAMPath>(in, output);
			compiler.addAnnotationProcessor(new JuzuProcessor());
		} catch(IOException e) {
			throw AbstractTestCase.failure(e);
		}
	}
	
	public RAMFileSystem getOutput() {
		return output;
	}
	
	public List<CompilationError> failCompile() {
		try {
			List<CompilationError> errors = compiler.compile();
			AbstractTestCase.assertTrue("Was expecting compilation to fail", errors.size() > 0);
			return errors;
		} catch(IOException e) {
			throw AbstractTestCase.failure(e);
		}
	}
	
	public void assertCompile() {
		try {
			Compiler<I, RAMPath> compiler = new Compiler<I, RAMPath>(input, output);
			compiler.addAnnotationProcessor(new JuzuProcessor());
			AbstractTestCase.assertEquals(Collections.emptyList(), compiler.compile());
			cl = new URLClassLoader(new URL[] { output.getURL() }, Thread.currentThread().getContextClassLoader());
		} catch(IOException e) {
			throw AbstractTestCase.failure(e);
		}
	}
	
	public Class<?> assertClass(String className) {
		try {
			return cl.loadClass(className);
		} catch(ClassNotFoundException e) {
			throw AbstractTestCase.failure(e);
		}
	}
}
