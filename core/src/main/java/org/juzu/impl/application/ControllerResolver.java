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
package org.juzu.impl.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.juzu.AmbiguousResolutionException;
import org.juzu.application.ApplicationDescriptor;
import org.juzu.application.Phase;
import org.juzu.impl.request.ControllerMethod;
import org.juzu.impl.request.ControllerParameter;
import org.juzu.impl.utils.Tools;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public class ControllerResolver {

	private final List<ControllerMethod> methods;
	
	private final ApplicationDescriptor desc;
	
	public ControllerResolver(ApplicationDescriptor desc) throws NullPointerException {
		if(desc == null) throw new NullPointerException("No null application descriptor accepted");
		
		//
		this.desc = desc;
		this.methods = desc.getControllerMethods();
	}
	
	public ControllerMethod resolve(Phase phase, Map<String, String[]> parameters) throws AmbiguousResolutionException {
		ControllerMethod found = null;
		String[] op = parameters.get("op");
		if(op != null && op.length > 0) {
			for(ControllerMethod method : methods) {
				if(method.getId().equals(op[0])) {
					found = method;
					break;
				}
			}
		} else if(phase == Phase.RENDER) {
			for(ControllerMethod method : methods) {
				if(method.getPhase() == Phase.RENDER &&
					 method.getName().equals("index") &&
					 method.getArgumentParameters().isEmpty()) {
					if(desc.getDefaultController() == method.getType()) {
						return method;
					} else if(found == null) {
						found = method;
					} else {
						throw new AmbiguousResolutionException();
					}
				}
			}
		}
		return found;
	}
}
