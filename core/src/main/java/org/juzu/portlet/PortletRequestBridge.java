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
package org.juzu.portlet;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;

import org.juzu.impl.request.RequestBridge;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public abstract class PortletRequestBridge<Rq extends PortletRequest, Rs extends PortletResponse> implements RequestBridge
{

	protected final Rq request;
	
	protected final Rs response;
	
	public PortletRequestBridge(Rq request, Rs response) {
		this.request = request;
		this.response = response;
	}
	
   public final Map<String, String[]> getParameters()
   {
	   return request.getParameterMap();
   }

   public void setFlashValue(Object key, Object value)
   {
   	Map<Object, Object> flash = (Map<Object, Object>)getSessionValue("flash");
   	if(flash == null) {
   		setSessionValue("flash", flash = new HashMap<Object, Object>());
   	}
   	flash.put(key, value);
   }
   
   public Object getFlashValue(Object key) {
   	Map<Object, Object> flash = (Map<Object, Object>)getSessionValue(key);
   	return flash != null ? flash.get(key) : null;
   }
   
   public Object getRequestValue(Object key) {
   	return getRequestContext().get(key);
   }
   
   public void setRequestValue(Object key, Object value) {
   	if(value == null) {
   		getRequestContext().remove(key);
   	} else {
   		getRequestContext().put(key, value);
   	}
   }

   private Map<Object, Object> getRequestContext()
   {
   	Map<Object, Object> store = (Map<Object, Object>)request.getAttribute("org.juzu.request_scope");
   	if(store == null) {
   		request.setAttribute("org.juzu.request_scope", store = new HashMap<Object, Object>());
   	}
	   return store;
   }
   
   public Object getSessionValue(Object key) {
   	return getSessionContext().get(key);
   }
   
   public void setSessionValue(Object key, Object value) {
   	if(value == null) {
   		getSessionContext().remove(key);
   	} else {
   		getSessionContext().put(key, value);
   	}
   }

   private Map<Object, Object> getSessionContext()
   {
   	PortletSession session = request.getPortletSession();
   	Map<Object, Object> store = (Map<Object, Object>) session.getAttribute("org.juzu.session_scope");
   	if(store == null) {
   		session.setAttribute("org.juzu.session_scope", store = new HashMap<Object, Object>());
   	}
	   return store;
   }

   public Object getIdentityValue(Object key) {
	   return null;
   }
   
   public void setIdentityValue(Object key, Object value) {
   }
}
