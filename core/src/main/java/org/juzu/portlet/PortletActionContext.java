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

import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.juzu.Response;
import org.juzu.impl.request.ActionContext;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public class PortletActionContext extends ActionContext
{
	
	private final ActionRequest actionrRequest;
	
	private final ActionResponse actionResponse;
	
	private ResponseImpl response;

   public PortletActionContext(ClassLoader classLoader, ActionRequest actionRequest, ActionResponse actionResponse)
   {
	   super(classLoader);
	   this.actionResponse = actionResponse;
	   this.actionrRequest = actionRequest;
	   this.response = null;
   }

   @Override
   public Response createResponse()
   {
	   return response;
   }

   @Override
   public Map<String, String[]> getParameters()
   {
	   return actionrRequest.getParameterMap();
   }
}
