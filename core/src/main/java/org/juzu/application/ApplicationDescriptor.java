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
package org.juzu.application;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import org.juzu.impl.request.ControllerMethod;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public class ApplicationDescriptor {
	
	private final Class<?> defaultController;

	private final String packageName;
	
	private final String name;
	
	private final List<ControllerMethod> controllerMethods;
	
	private final String templatesPackageName;
	
	public ApplicationDescriptor(
		String packageName, 
		String name, 
		Class<?> defaultController,
		String templatesPackageName, 
		List<ControllerMethod> controllerMethods) {
		
		this.packageName = packageName;
		this.name = name;
		this.defaultController = defaultController;
		this.templatesPackageName = templatesPackageName;
		this.controllerMethods = Collections.unmodifiableList(controllerMethods);
	}
	
	public Class<?> getDefaultController() {
		return defaultController;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getName() {
		return name;
	}

	public List<ControllerMethod> getControllerMethods() {
		return controllerMethods;
	}
	
	public ControllerMethod getControllerMethod(Class<?> type, String name, Class<?> ... parameterType) {
		for(int i = 0; i < controllerMethods.size(); i++) {
			ControllerMethod cm = controllerMethods.get(i);
			Method m = cm.getMethod();
			if(type.equals(cm.getType()) && m.getName().equals(name)) {
				Class<?>[] a = m.getParameterTypes();
				if(a.length == parameterType.length) {
					for(int j = 0; j < parameterType.length; j++) {
						if(!a[j].equals(parameterType[j])) continue;
					}
					return cm;
				}
			}
		}
		return null;
	}

	public String getTemplatesPackageName() {
		return templatesPackageName;
	}
}
