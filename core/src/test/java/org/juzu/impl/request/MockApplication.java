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
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

import org.juzu.application.ApplicationDescriptor;
import org.juzu.impl.application.ApplicationContext;
import org.juzu.impl.application.Bootstrap;
import org.juzu.impl.spi.cdi.Container;
import org.juzu.impl.spi.fs.ReadFileSystem;
import org.juzu.impl.spi.fs.disk.DiskFileSystem;
import org.juzu.test.AbstractTestCase;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public class MockApplication<P>
{
	private final ReadFileSystem<P> classes;
	
	final ClassLoader classLoader;
	
	private ApplicationContext applicationContext;
	
	public MockApplication(ReadFileSystem<P> classes, ClassLoader classLoader) {
		this.classes = classes;
		this.classLoader = classLoader;
	}
	
	public void init() throws Exception {
		P f = classes.getFile(Arrays.asList("org", "juzu"), "config.properties");
		if(f == null) throw new Exception("Cannot find config properties");
		
		URL url = classes.getURL(f);
		InputStream in = url.openStream();
		Properties props = new Properties();
		props.load(in);
		
		if(props.size() != 1) throw AbstractTestCase.failure("Could not find an application to start " + props);
		Map.Entry<Object, Object> entry = props.entrySet().iterator().next();
		
		String fqn = entry.getValue().toString();
		System.out.println("Loading class descriptor " + fqn);
		Class<?> clazz = classLoader.loadClass(fqn);
		Field field = clazz.getDeclaredField("DESCRIPTOR");
		ApplicationDescriptor descriptor = (ApplicationDescriptor)field.get(null);
		
		DiskFileSystem libs = new DiskFileSystem(new File(Bootstrap.class.getProtectionDomain().getCodeSource().getLocation().toURI()));
		Container container = new org.juzu.impl.spi.cdi.weld.WeldContainer(classLoader);
		container.addFileSystem(classes);
		container.addFileSystem(libs);
		
		Bootstrap bootstrap = new Bootstrap(container, descriptor);
		bootstrap.start();
		applicationContext = bootstrap.getContext();
	}
	
	void invoke(RequestContext<?> context) {
		applicationContext.invoke(context);
	}
	
	public MockClient client() {
		return new MockClient(this);
	}
}
