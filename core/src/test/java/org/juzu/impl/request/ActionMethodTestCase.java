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
import java.util.Arrays;
import java.util.Collections;

import org.juzu.application.ApplicationDescriptor;
import org.juzu.impl.spi.fs.disk.DiskFileSystem;
import org.juzu.test.CompilerHelper;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public class ActionMethodTestCase extends TestCase {

	@Override
	public void setUp() throws Exception {
		File root = new File(System.getProperty("test.resources"));
		DiskFileSystem in = new DiskFileSystem(root, "controller2");

		//
		CompilerHelper<File> compiler = new CompilerHelper<File>(in);
		compiler.assertCompile();
		aClass = compiler.assertClass("controller2.A");
		compiler.assertClass("controller2.A_");
		
		//
		Class<?> appClass = compiler.assertClass("controller2.Controller2Application");
		descriptor = (ApplicationDescriptor)appClass.getDeclaredField("DESCRIPTOR").get(null);
	}
	
	private Class<?> aClass;
	
	private ApplicationDescriptor descriptor;
	
	public void testNoArg() throws Exception {
		ControllerMethod cm = descriptor.getControllerMethod(aClass, "noArg");
		assertEquals("noArg", cm.getName());
		assertEquals(Collections.emptyList(), cm.getArgumentParameters());
	}
	
	public void testStringArg() throws Exception {
		ControllerMethod cm = descriptor.getControllerMethod(aClass, "oneArg", String.class);
		assertEquals("oneArg", cm.getName());
		assertEquals(Arrays.asList(new ControllerParameter("foo")), cm.getArgumentParameters());
	}
}
