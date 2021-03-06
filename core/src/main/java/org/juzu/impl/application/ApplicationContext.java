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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Singleton;

import org.juzu.Path;
import org.juzu.RenderScoped;
import org.juzu.application.ApplicationDescriptor;
import org.juzu.impl.cdi.Export;
import org.juzu.impl.cdi.ScopeController;
import org.juzu.impl.request.ActionContext;
import org.juzu.impl.request.ControllerMethod;
import org.juzu.impl.request.ControllerParameter;
import org.juzu.impl.request.MimeContext;
import org.juzu.impl.request.RenderContext;
import org.juzu.impl.request.RequestContext;
import org.juzu.impl.request.ResourceContext;
import org.juzu.impl.spi.cdi.Container;
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
	
	private final ControllerResolver resolver;
	
	private static final ThreadLocal<RequestContext> current = new ThreadLocal<RequestContext>();
	
	public static RequestContext getCurrentRequest() {
		return current.get();
	}
	public ApplicationContext() {
		
		Bootstrap bootstrap = Bootstrap.foo.get();

		//
		this.descriptor = bootstrap.descriptor;
		this.container = bootstrap.container;
		this.resolver = new ControllerResolver(bootstrap.descriptor);
	}
	
	public ApplicationDescriptor getDescriptor() {
		return descriptor;
	}
	
	public void invoke(RequestContext context) {
		ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(context.getClassLoader());
			current.set(context);
			ScopeController.begin(context);
			doInvoke(context);
		} finally {
			current.set(null);
			ScopeController.end();
			Thread.currentThread().setContextClassLoader(oldCL);
		}
	}
	
	private void doInvoke(RequestContext context) {
		ControllerMethod method = resolver.resolve(context.getPhase(), context.getParameters());
		if(method == null) {
			StringBuilder sb = new StringBuilder("handle me gracefully: no method could be resolved for phase " + context.getPhase() + 
				"and parameters={");
			int index = 0;
			for(Map.Entry<String, String[]> entry : ((Map<String, String[]>)context.getParameters()).entrySet()) {
				if(index++ > 0) sb.append(',');
				sb.append(entry.getKey()).append('=').append(Arrays.asList(entry.getValue()));
			}
			sb.append("}");
			throw new UnsupportedOperationException(sb.toString());
		}
		else {
			Class<?> type = method.getType();
			BeanManager mgr = container.getManager();
			Set<? extends Bean> beans = mgr.getBeans(type);
			if(beans.size() == 1) {
				try {
					//Get the bean
					Bean bean = beans.iterator().next();
					CreationalContext<?> cc = mgr.createCreationalContext(bean);
					Object o = mgr.getReference(bean, type, cc);
					
					//Prepare the method arguments
					List<ControllerParameter> params = method.getArgumentParameters();
					Object[] args = new Object[params.size()];
					for(int i = 0; i < args.length; i++) {
						Map<String, String[]> map = context.getParameters();
						String[] values = map.get(params.get(i).getName());
						args[i] = (values != null && values.length > 0) ? values[0] : null;
					}
					
					//
					method.getMethod().invoke(o, args);
				} catch(Exception e) {
					throw new UnsupportedOperationException("handle me gracefully", e);
				}
			}
		}
	}
	
	@Produces
	@RenderScoped
	public Printer getPrinter() {
		RequestContext context = current.get();
		if(context instanceof MimeContext) {
			return ((MimeContext) context).getPrinter();
		} else throw new AssertionError("dose not make sense");
	}
	
	@Produces
	public Template getRenderer(InjectionPoint point) {
		Bean<?> bean = point.getBean();
		Path template = point.getAnnotated().getAnnotation(Path.class);
		StringBuilder id = new StringBuilder(descriptor.getTemplatesPackageName());
		if(id.length() > 0)  id.append('.');
		id.append(template.value(), 0, template.value().indexOf('.'));
		return new Template(id.toString());
	}
}
