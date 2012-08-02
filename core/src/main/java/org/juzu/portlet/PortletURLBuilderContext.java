/*
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.juzu.portlet;

import javax.portlet.RenderResponse;

import org.juzu.URLBuilder;
import org.juzu.application.Phase;
import org.juzu.impl.request.URLBuilderContext;

/**
 * Author : Nguyen Thanh Hai
 *          haithanh0809@gmail.com
 * Aug 1, 2012  
 */

public class PortletURLBuilderContext implements URLBuilderContext {
	
	private final RenderResponse response;
	
	public PortletURLBuilderContext(RenderResponse response) {
		this.response = response;
	}

	public URLBuilder createURLBuilder(Phase phase) {
		switch(phase) {
			case ACTION:
				return new PortletURLBuilder(response.createActionURL());
			case RENDER:
				return new PortletURLBuilder(response.createRenderURL());
			default:
				throw new AssertionError();
		}
	}
}
