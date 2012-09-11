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

import java.lang.reflect.Method;
import java.util.List;

import org.juzu.application.Phase;
import org.juzu.impl.utils.Tools;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public class ControllerMethod {

	private final Phase phase;
	
	private final Class<?> type;
	
	private final Method method;
	
	private final List<ControllerParameter> argumentParameters;
	
	public ControllerMethod(
		Phase phase, Class<?> type, 
		Method method, 
		List<ControllerParameter> argumentParameters) {
		if(type == null) throw new NullPointerException();
		if(method == null) throw new NullPointerException();
		
		this.phase = phase;
		this.type = type;
		this.method = method;
		this.argumentParameters = Tools.safeUnmodifiableList(argumentParameters);
	}
	
	public Phase getPhase() {
		return phase;
	}
	
	public Class<?> getType() {
		return type;
	}
	
	public Method getMethod() {
		return method;
	}
	
	public String getMethodName() {
		return method.getName();
	}
	
	public List<ControllerParameter> getArgumentParameters() {
		return argumentParameters;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[type=" + type.getName() + ", method=" + method + "]";
	}
}
