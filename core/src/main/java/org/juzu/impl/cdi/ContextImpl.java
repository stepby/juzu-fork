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

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;

import org.juzu.application.Phase;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public class ContextImpl implements Context {
	
	private final ScopeController controller;
	
	private final Phase phase;
	
	private final Class<? extends Annotation> scopeType;
	
	public ContextImpl(ScopeController controller, Phase phase, Class<? extends Annotation> scopeType) {
		this.controller = controller;
		this.phase = phase;
		this.scopeType = scopeType;
	}

	public Class<? extends Annotation> getScope() {
		return scopeType;
	}

	public <T> T get(Contextual<T> contextual, CreationalContext<T> creationalContext) {
		return controller.get(phase, contextual, creationalContext);
	}

	public <T> T get(Contextual<T> contextual) {
		return get(contextual, null);
	}

	public boolean isActive() {
		return controller.currentPhase.get() == phase;
	}
}
