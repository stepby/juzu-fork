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
package org.juzu.portlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarFile;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.juzu.application.ApplicationContext;
import org.juzu.application.ApplicationDescriptor;
import org.juzu.application.Bootstrap;
import org.juzu.impl.spi.cdi.Container;
import org.juzu.impl.spi.fs.jar.JarFileSystem;
import org.juzu.impl.spi.fs.war.WarFileSystem;
import org.juzu.request.RenderContext;
import org.juzu.text.Printer;
import org.juzu.text.WriterPrinter;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public class JuzuPortlet implements Portlet {
	
	private ApplicationContext applicationContext;

	public void init(PortletConfig config) throws PortletException {
		//Find an application
		Properties props;
		try {
			URL url = Thread.currentThread().getContextClassLoader().getResource("org/juzu/config.properties");
			InputStream is = url.openStream();
			props = new Properties();
			props.load(is);
		} catch(IOException e) {
			throw new PortletException("Could not find an application to start", e);
		}
		
		//
		if(props.size() != 1) throw new PortletException("Could not find an application to start " + props);
		
		//
		Map.Entry<Object, Object> entry = props.entrySet().iterator().next();
		ApplicationDescriptor descriptor;
		String fqn = entry.getValue().toString();
		try {
			Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(fqn);
			Field field = clazz.getField("DESCRIPTOR");
			descriptor = (ApplicationDescriptor)field.get(null);
		} catch(Exception e) {
			throw new PortletException("Could not find an appliction to start " + fqn, e);
		}
		
		//
		Container container;
		try {
			URL url = Bootstrap.class.getProtectionDomain().getCodeSource().getLocation();
			File f = new File(url.toURI());
			JarFileSystem jarFS = new JarFileSystem(new JarFile(f));
			
			//
			WarFileSystem fs = WarFileSystem.create(config.getPortletContext(), "WEB-INF/classes");
			
			//
			container = new org.juzu.impl.spi.cdi.weld.WeldContainer();
			container.addFileSystem(fs);
			container.addFileSystem(jarFS);
			
			//
			System.out.println("Starting application [" + descriptor.getName() + "]");
			Bootstrap bootstrap = new Bootstrap(container, descriptor);
			bootstrap.start();
			applicationContext = bootstrap.getContext();
		} catch(Exception e) {
			throw new PortletException("Error when starting application [" + descriptor.getName() + "]", e);
		}
	}

	public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
		throw new UnsupportedOperationException("Not implement");
	}

	public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		Printer printer = new WriterPrinter(response.getWriter());
		
		RenderContext renderContext = new RenderContext(request.getParameterMap(), printer);
		applicationContext.invoke(renderContext);
	}

	public void destroy() {
		
	}
}
