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

import org.juzu.application.Phase;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public enum Scope
{
	RENDER() {
		@Override
      public boolean isActive(RequestContext<?> context)
      {
	      return context.getPhase() == Phase.RENDER;
      }
	},
	
	ACTION() {
		@Override
      public boolean isActive(RequestContext<?> context)
      {
	      return context.getPhase() == Phase.ACTION;
      }
	},
	
	REQUEST() {
		@Override
      public boolean isActive(RequestContext<?> context)
      {
	      return true;
      }
		
	},
	
	MIME() {
		@Override
      public boolean isActive(RequestContext<?> context)
      {
	      return context.getPhase() == Phase.RESOURCE;
      }
		
	},
	
	RESOURCE() {
		@Override
      public boolean isActive(RequestContext<?> context)
      {
	      return context.getPhase() == Phase.RESOURCE;
      }
	},
	
	SESSION() {
		@Override
      public boolean isActive(RequestContext<?> context)
      {
	      return true;
      }
	},
	
	FLASH() {
		@Override
      public boolean isActive(RequestContext<?> context)
      {
	      return true;
      }
	},
	
	IDENTITY() {
		@Override
      public boolean isActive(RequestContext<?> context)
      {
	      return false;
      }
	};
	
	public abstract boolean isActive(RequestContext<?> context);
}
