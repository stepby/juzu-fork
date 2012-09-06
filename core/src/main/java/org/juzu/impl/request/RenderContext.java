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

import java.util.List;
import java.util.Map;

import org.juzu.URLBuilder;
import org.juzu.application.Phase;
import org.juzu.text.Printer;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public abstract class RenderContext extends RequestContext {
	
	public RenderContext(ClassLoader classLoader) {
		super(classLoader);
	}
	
	public abstract Printer getPrinter();

	@Override
	public final Phase getPhase() {
		return Phase.RENDER;
	}
	
	protected abstract URLBuilder createURLBuilder(Phase phase);
	
	public final URLBuilder createURLBuilder(ControllerMethod method) {
		URLBuilder builder = createURLBuilder(method.getPhase());
		List<ControllerParameter> parameters = method.getAnnotationParameters();
		for(int i = 0; i < parameters.size(); i++) {
			ControllerParameter parameter = parameters.get(i);
			builder.setParameter(parameter.getName(), parameter.getValue());
		}
		
		//
		return builder;
	}
	
	public final URLBuilder createURLBuilder(ControllerMethod method, Object arg) {
		URLBuilder builder = createURLBuilder(method);
		
		//
		ControllerParameter param = method.getArgumentParameters().get(0);
		if(arg != null) {
			builder.setParameter(param.getName(), String.valueOf(arg));
		}
		
		//
		return builder;
	}
	
	public URLBuilder createURLBuilder(ControllerMethod method, Object[] args) {
		URLBuilder builder = createURLBuilder(method);
		
		//Fill in argument parameters
		for(int i = 0; i < args.length; i++) {
			if(args[i] != null) {
				builder.setParameter(method.getArgumentParameters().get(i).getName(), String.valueOf(args[i]));
			}
		}
		
		return builder;
	}
}
