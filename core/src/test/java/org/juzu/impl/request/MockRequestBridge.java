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

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public class MockRequestBridge implements RequestBridge
{
	
	private final MockClient client;
	
	private final Map<String, String[]> parameters;
	
	private final Map<Object, Object> attributes;
	
	public MockRequestBridge(MockClient client) {
		this.client = client;
		this.parameters = new HashMap<String, String[]>();
		this.attributes = new HashMap<Object, Object>();
	}
	
	public Map<Object, Object> getAttributes() {
		return attributes;
	}
	
   public Map<String, String[]> getParameters()
   {
	   return parameters;
   }

   public Object getFlashValue(Object key)
   {
	   return client.getFlashValue(key);
   }

   public void setFlashValue(Object key, Object value)
   {
	   client.setFlashValue(key, value);
   }

   public Object getRequestValue(Object key)
   {
	   return attributes.get(key);
   }

   public void setRequestValue(Object key, Object value)
   {
   	if(value != null) {
   		attributes.put(key, value);
   	} else {
   		attributes.remove(key);
   	}
   }

   public Object getSessionValue(Object key)
   {
	   return client.getSession().get(key);
   }

   public void setSessionValue(Object key, Object value)
   {
	   if(value != null) {
	   	client.getSession().put(key, value);
	   } else {
	   	client.getSession().remove(key);
	   }
   }

   public Object getIdentityValue(Object key)
   {
	   return null;
   }

   public void setIdentityValue(Object key, Object value)
   {
   }
}
