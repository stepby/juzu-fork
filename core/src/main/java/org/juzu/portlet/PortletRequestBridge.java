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
public class PortletRequestBridge<Rq extends PortletRequest, Rs extends PortletResponse> implements RequestBridge
{

	protected final Rq request;
	
	protected final Rs response;
	
	public PortletRequestBridge(Rq request, Rs response) {
		this.request = request;
		this.response = response;
	}
	
   public Map<String, String[]> getParameters()
   {
	   return request.getParameterMap();
   }

   public Map<Object, Object> getFlashContext()
   {
	   return null;
   }

   public Map<Object, Object> getRequestContext()
   {
   	Map<Object, Object> store = (Map<Object, Object>)request.getAttribute("org.juzu.request_scope");
   	if(store == null) {
   		request.setAttribute("org.juzu.request_scope", store = new HashMap<Object, Object>());
   	}
	   return store;
   }

   public Map<Object, Object> getSessionContext()
   {
   	PortletSession session = request.getPortletSession();
   	Map<Object, Object> store = (Map<Object, Object>) session.getAttribute("org.juzu.session_scope");
   	if(store == null) {
   		session.setAttribute("org.juzu.session_scope", store = new HashMap<Object, Object>());
   	}
	   return store;
   }

   public Map<Object, Object> getIdentityContext()
   {
	   return null;
   }

}
