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
public class RenderContext extends RequestContext {
	
	private final Printer printer;
	
	private URLBuilderContext urlBuilderContext;

	public RenderContext(ClassLoader classLoader, Map<String, String[]> parameters, Printer printer, URLBuilderContext urlBuilderContext) {
		super(classLoader, parameters);
		
		//
		this.printer = printer;
		this.urlBuilderContext = urlBuilderContext;
	}
	
	public Printer getPrinter() {
		return printer;
	}

	@Override
	public Phase getPhase() {
		return Phase.RENDER;
	}
	
	public URLBuilder createURLBuilder(ControllerMethod method) {
		URLBuilder builder = urlBuilderContext.createURLBuilder(method.getPhase());
		List<ControllerParameter> parameters = method.getAnnotationParameters();
		for(int i = 0; i < parameters.size(); i++) {
			ControllerParameter parameter = parameters.get(i);
			builder.setParameter(parameter.getName(), parameter.getValue());
		}
		
		//
		return builder;
	}
	
	public URLBuilder createURLBuilder(ControllerMethod method, Object value) {
		URLBuilder builder = createURLBuilder(method);
		
		//
		ControllerParameter param = method.getArgumentParameters().get(0);
		if(value != null) {
			builder.setParameter(param.getName(), String.valueOf(value));
		}
		
		//
		return builder;
	}
	
	public URLBuilder createURLBuilder(ControllerMethod method, Object[] values) {
		URLBuilder builder = createURLBuilder(method);
		
		//Fill in argument parameters
		for(int i = 0; i < values.length; i++) {
			if(values[i] != null) {
				builder.setParameter(method.getArgumentParameters().get(i).getName(), String.valueOf(i));
			}
		}
		
		return builder;
	}
}
