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

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public interface RequestBridge
{
	Map<String, String[]> getParameters();
	
	Object getFlashValue(Object key);
	
	void setFlashValue(Object key, Object value);
	
	Object getRequestValue(Object key);
	
	void setRequestValue(Object key, Object value);
	
	Object getSessionValue(Object key);
	
	void setSessionValue(Object key, Object value);
	
	Object getIdentityValue(Object key);
	
	void setIdentityValue(Object key, Object value);
}
