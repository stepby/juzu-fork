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
package org.juzu.impl.cdi;

import java.util.Collections;
import java.util.Map;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;

import org.juzu.ActionScoped;
import org.juzu.MimeScoped;
import org.juzu.RenderScoped;
import org.juzu.ResourceScoped;
import org.juzu.impl.request.RequestContext;
import org.juzu.impl.request.Scope;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public class ScopeController {
	
	private static final Map<Contextual<?>, Object> EMPTY_MAP = Collections.emptyMap();
	
	static final ScopeController INSTANCE = new ScopeController();
	
	final ContextImpl requestContext = new ContextImpl(this, Scope.REQUEST, RequestScoped.class);
	
	final ContextImpl renderContext = new ContextImpl(this, Scope.RENDER, RenderScoped.class);
	
	final ContextImpl resourceContext = new ContextImpl(this, Scope.RESOURCE, ResourceScoped.class);
	
	final ContextImpl mimeContext = new ContextImpl(this, Scope.MIME, MimeScoped.class);
	
	final ContextImpl actionContext = new ContextImpl(this, Scope.ACTION, ActionScoped.class);
	
	final ContextImpl sessionContext = new ContextImpl(this, Scope.SESSION, SessionScoped.class);
	
	final ThreadLocal<RequestContext> currentContext = new ThreadLocal<RequestContext>();
	
	public static ScopeController getInstance() {
		return INSTANCE;
	}
	
	public static void begin(RequestContext context) throws IllegalStateException {
		if(context == null) throw new NullPointerException();
		
		if(INSTANCE.currentContext.get() != null) throw new IllegalStateException("Already started");
	
		INSTANCE.currentContext.set(context);
	}
	
	public static void end() throws IllegalStateException {
		INSTANCE.currentContext.set(null);
	}

	public <T> T get(Scope scope, Contextual<T> contextual, CreationalContext<T> creationalContext) {
		RequestContext ctx = currentContext.get();
		if(ctx == null) throw new ContextNotActiveException();
		
		Map<Object, Object> map = ctx.getContext(scope);
		if(map == null) throw new ContextNotActiveException();

		Object o = map.get(contextual);
		if(o == null) {
			if(creationalContext != null) {
				o = contextual.create(creationalContext);
				map.put(contextual, o);
			}
		}
		return (T)o;
	}

	public boolean isActive(Scope scope) {
		RequestContext ctx = currentContext.get();
		if(ctx == null) throw new ContextNotActiveException();
		return ctx.getContext(scope) != null;
	}
}
