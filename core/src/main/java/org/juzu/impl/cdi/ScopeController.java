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
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;

import org.juzu.application.Phase;
import org.juzu.request.ActionScoped;
import org.juzu.request.RenderScoped;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public class ScopeController {
	
	private static final Map<Contextual<?>, Object> EMPTY_MAP = Collections.emptyMap();
	
	static final ScopeController INSTANCE = new ScopeController();
	
	final ThreadLocal<Map<Contextual<?>, Object>> current = new ThreadLocal<Map<Contextual<?>,Object>>();
	
	final ThreadLocal<Phase> currentPhase = new ThreadLocal<Phase>();
	
	final ContextImpl resquestContext = new ContextImpl(this, null, RequestScoped.class);
	
	final ContextImpl actionContext = new ContextImpl(this, Phase.ACTION, ActionScoped.class);
	
	final ContextImpl renderContext = new ContextImpl(this, Phase.RENDER, RenderScoped.class);
	
	public static ScopeController getInstance() {
		return INSTANCE;
	}
	
	public static void start(Phase phase) throws IllegalStateException {
		if(phase == null) throw new NullPointerException();
		if(INSTANCE.current.get() != null) throw new IllegalStateException("Already started");
		INSTANCE.current.set(EMPTY_MAP);
		INSTANCE.currentPhase.set(phase);
	}
	
	public static void stop() throws IllegalStateException {
		INSTANCE.current.set(null);
		INSTANCE.currentPhase.set(null);
	}

	public <T> T get(Phase phase, Contextual<T> contextual, CreationalContext<T> creationalContext) {
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

	public boolean isActive(Phase phase) {
		return phase == null || currentPhase.get() == phase;
	}
}
