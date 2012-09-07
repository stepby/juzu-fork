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
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;

import junit.framework.TestCase;

import org.juzu.application.PhaseLiteral;
import org.juzu.impl.spi.fs.disk.DiskFileSystem;
import org.juzu.test.CompilerHelper;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public class RenderMethodTestCase extends TestCase {
	
	@Override
	public void setUp() throws Exception {
		File root = new File(System.getProperty("test.resources"));
		DiskFileSystem in = new DiskFileSystem(root, "controller1");

		//
		CompilerHelper<File> compiler = new CompilerHelper<File>(in);
		compiler.assertCompile();
		compiler.assertClass("controller1.A");
		a_Class = compiler.assertClass("controller1.A_");
	}
	
	private Class<?> a_Class;
	
	public void testNoArg() throws Exception {
		Field f = a_Class.getDeclaredField("noArg");
		PhaseLiteral l = (PhaseLiteral)f.get(null);
		
		ControllerMethod cm = l.getDescriptor();
		assertEquals("noArg", cm.getMethodName());
		assertEquals(Collections.emptyList(), cm.getArgumentParameters());
	}
	
	public void testStringArg() throws Exception {
		Field f = a_Class.getDeclaredField("oneArg");
		PhaseLiteral l = (PhaseLiteral) f.get(null);
		
		ControllerMethod cm = l.getDescriptor();
		assertEquals("oneArg", cm.getMethodName());
		assertEquals(Arrays.asList(new ControllerParameter("foo")), cm.getArgumentParameters());
	}
}
