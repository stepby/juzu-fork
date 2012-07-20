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

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public class InvocationContext implements Context {
	
	private static final ThreadLocal<Map<Contextual<?>, Object>> current = new ThreadLocal<Map<Contextual<?>,Object>>();
	
	private static final Map<Contextual<?>, Object> EMPTY_MAP = Collections.emptyMap();
	
	private static final InvocationContext INSTANCE = new InvocationContext();
	
	public static InvocationContext getInstance() {
		return INSTANCE;
	}
	
	public static void start() throws IllegalStateException {
		if(INSTANCE.current.get() != null) throw new IllegalStateException("Already started");
		INSTANCE.current.set(EMPTY_MAP);
	}
	
	public static void stop() throws IllegalStateException {
		INSTANCE.current.set(null);
	}

	public Class<? extends Annotation> getScope() {
		return InvocationScoped.class;
	}

	public <T> T get(Contextual<T> contextual, CreationalContext<T> creationalContext) {
		Map<Contextual<?>, Object> map = current.get();
		if(map == null) throw new ContextNotActiveException();
		Object o = map.get(contextual);
		if(o == null) {
			if(creationalContext != null) {
				o = contextual.create(creationalContext);
				if(map == EMPTY_MAP) {
					current.set(map = new HashMap<Contextual<?>, Object>());
				}
				map.put(contextual, o);
			}
		}
		return (T)o;
	}

	public <T> T get(Contextual<T> contextual) {
		return get(contextual, null);
	}

	public boolean isActive() {
		return current.get() != null;
	}
}
