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

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Singleton;

import org.juzu.Resource;
import org.juzu.impl.cdi.Export;
import org.juzu.impl.cdi.InvocationContext;
import org.juzu.impl.cdi.InvocationScoped;
import org.juzu.impl.spi.cdi.Container;
import org.juzu.request.ActionContext;
import org.juzu.request.RenderContext;
import org.juzu.request.RequestContext;
import org.juzu.template.Template;
import org.juzu.text.Printer;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
@Export
@Singleton
public class ApplicationContext {

	private final ApplicationDescriptor descriptor;
	
	private final Container container;
	
	private static final ThreadLocal<RequestContext> current = new ThreadLocal<RequestContext>();
	
	public static RequestContext getCurrentRequest() {
		return current.get();
	}
	
	public ApplicationContext() {
		Bootstrap bootstrap = Bootstrap.foo.get();
		//
		this.descriptor = bootstrap.descriptor;
		this.container = bootstrap.container;
	}
	
	public ApplicationDescriptor getDescriptor() {
		return descriptor;
	}
	
	/**
	 *  For now pretty simple resolution algorithm
	 * @param data the data
	 * @return the render descriptor or null if nothing could be resolved
	 */
	public ControllerMethod resolve(Phase phase, Map<String, String[]> data) {
		for(ControllerMethod method : descriptor.getControllerMethods()) {
			if(method.getPhase() == phase) return method;
		}
		return null;
	}
	
	public void invoke(RequestContext context) {
		try {
			current.set(context);
			InvocationContext.start();
			if(context instanceof RenderContext) {
				doInvoke((RenderContext) context);
			} else if(context instanceof ActionContext) {
				doInvoke(context);
			} else throw new UnsupportedOperationException();
		} finally {
			current.set(null);
			InvocationContext.stop();
		}
	}
	
	private void doInvoke(RequestContext context) {
		ControllerMethod method = resolve(context.getPhase(), context.getParameters());
		if(method == null) throw new UnsupportedOperationException("handle me gracefully");
		else {
			Class<?> type = method.getType();
			System.out.println("type = " + type);
			BeanManager mgr = container.getManager();
			Set<? extends Bean> beans = mgr.getBeans(type);
			if(beans.size() == 1) {
				try {
					//Get the bean
					Bean bean = beans.iterator().next();
					CreationalContext<?> cc = mgr.createCreationalContext(bean);
					Object o = mgr.getReference(bean, type, cc);
					
					//For now we do only zero arg invocations
					method.getMethod().invoke(o);
				} catch(Exception e) {
					throw new UnsupportedOperationException("handle me gracefully", e);
				}
			}
		}
	}
	
	@Produces
	@InvocationScoped
	public Printer getPrinter() {
		RequestContext context = current.get();
		if(context instanceof RenderContext) {
			return ((RenderContext) context).getPrinter();
		} else throw new UnsupportedOperationException("handle me gracefully");
	}
	
	@Produces
	public Template getRenderer(InjectionPoint point) {
		Bean<?> bean = point.getBean();
		Resource template = point.getAnnotated().getAnnotation(Resource.class);
		StringBuilder id = new StringBuilder(descriptor.getPackageName());
		if(id.length() > 0)  id.append('.');
		id.append(template.value(), 0, template.value().indexOf('.'));
		return new Template(id.toString());
	}
}
