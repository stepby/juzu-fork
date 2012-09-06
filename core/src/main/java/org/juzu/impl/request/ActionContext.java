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

import org.juzu.Response;
import org.juzu.application.Phase;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public abstract class ActionContext extends RequestContext {

	public ActionContext(ClassLoader classLoader) {
		super(classLoader);
	}

	@Override
	public Phase getPhase() {
		return Phase.ACTION;
	}
	
	public void map(Response response, ControllerMethod method) {
		List<ControllerParameter> annotationParameters = method.getAnnotationParameters();
		for(int i = 0; i < annotationParameters.size(); i++) {
			ControllerParameter annotationParameter = annotationParameters.get(i);
			response.setParameter(annotationParameter.getName(), annotationParameter.getValue());
		}
	}
	
	public Response createResponse(ControllerMethod method) {
		Response response = createResponse();
		map(response, method);
		return response;
	}
	
	public Response createResponse(ControllerMethod method, Object arg) {
		Response response = createResponse();
		map(response, method);
		if(arg != null) {
			ControllerParameter param = method.getArgumentParameters().get(0);
			response.setParameter(param.getName(), arg.toString());
		}
		return response;
	}
	
	public Response createResponse(ControllerMethod method, Object[] args) {
		Response response = createResponse();
		map(response, method);
		List<ControllerParameter> argumentParameters = method.getArgumentParameters();
		for(int i = 0; i < argumentParameters.size(); i++) {
			ControllerParameter argParameter = argumentParameters.get(i);
			response.setParameter(argParameter.getName(), args[i].toString());
		}
		return response;
	}
	
	public abstract Response createResponse();
}
