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
public class CompilerHelper<S> {

	private ReadFileSystem<S> in;
	
	private RAMFileSystem out;
	
	private ClassLoader cl;
	
	public CompilerHelper(ReadFileSystem<S> in) {
		try {
			this.in = in;
			this.out = new RAMFileSystem();
		} catch(IOException e) {
			throw AbstractTestCase.failure(e);
		}
	}
	
	public List<CompilationError> failCompile() {
		try {
			Compiler<S, RAMPath> compiler = new Compiler<S, RAMPath>(in, out);
			compiler.addAnnotationProcessor(new JuzuProcessor());
			List<CompilationError> errors = compiler.compile();
			AbstractTestCase.assertTrue("Was expecting compilation to fail", errors.size() > 0);
			return errors;
		} catch(IOException e) {
			throw AbstractTestCase.failure(e);
		}
	}
	
	public void assertCompile() {
		try {
			Compiler<S, RAMPath> compiler = new Compiler<S, RAMPath>(in, out);
			compiler.addAnnotationProcessor(new JuzuProcessor());
			AbstractTestCase.assertEquals(Collections.emptyList(), compiler.compile());
			cl = new URLClassLoader(new URL[] { out.getURL() }, Thread.currentThread().getContextClassLoader());
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
