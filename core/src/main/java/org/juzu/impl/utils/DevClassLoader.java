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
package org.juzu.impl.utils;

import java.net.URL;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public class DevClassLoader extends ClassLoader {

	public DevClassLoader(ClassLoader classLoader) {
		super(classLoader);
	}
	
	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		Class<?> found = super.loadClass(name, resolve);
		
		//
		if(found.getClassLoader() == super.getParent()) {
			String classPath = name.replace('.', '/') + ".class";
			URL url = getResource(classPath);
			if(url == null) throw new ClassNotFoundException();
		}
		
		//
		return found;
	}
	
	@Override
	public URL getResource(String name) {
		URL url = super.getResource(name);
		if(url != null && shouldLoad(url, name)) {
			return url;
		} 
		return null;
	}
	
	private boolean shouldLoad(URL url, String name) {
		//Unwrap until we get the file location
		String protocal = url.getProtocol();
		if("file".equals(protocal)) {
			String path = url.getPath();
			if(path.endsWith("/WEB-INF/classes/" + name)) {
				return false;
			} else {
				return true;
			}
		} else if("jar".equals(protocal)) {
			String path = url.getPath();
			int index = path.indexOf("!/");
			String nested = path.substring(0, index);
			if(nested.endsWith(".jar")) {
				return true;
			} else {
				throw new UnsupportedOperationException("handle me gracefully " + url);
			}
		} else {
			throw new UnsupportedOperationException("handle me gracefully " + url);
		}
	}
}
