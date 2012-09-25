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

import java.util.Map;

import org.juzu.application.Phase;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public abstract class RequestContext<B extends RequestBridge> {

	protected final ClassLoader classLoader;
	
	protected final B bridge;
	
	public RequestContext(ClassLoader classLoader, B bridge) {
		this.classLoader = classLoader;
		this.bridge = bridge;
	}
	
	public final Map<String, String[]> getParameters() {
		return bridge.getParameters();
	}
	
	public final ClassLoader getClassLoader() {
		return classLoader;
	}
	
	public abstract Phase getPhase();
	
	public final Object getContextualValue(Scope scope, Object key) {
		switch (scope)
      {
			case FLASH :
				return bridge.getFlashValue(key);
			case REQUEST:
			case MIME:
			case RENDER:
			case ACTION:
			case RESOURCE:
				return bridge.getRequestValue(key);
			case SESSION:
				return bridge.getSessionValue(key);
			case IDENTITY:
				return bridge.getIdentityValue(key);
			default :
				throw new AssertionError();
		}
	}
	
	public final void setContextualValue(Scope scope, Object key, Object value) {
		switch (scope)
      {
			case FLASH :
				bridge.setFlashValue(key, value);
				break;
			case REQUEST:
			case MIME:
			case RENDER:
			case ACTION:
			case RESOURCE:
				bridge.setRequestValue(key, value);
				break;
			case SESSION:
				bridge.setSessionValue(key, value);
				break;
			case IDENTITY:
				bridge.setIdentityValue(key, value);
				break;
			default :
				throw new AssertionError();
		}
	}
}
