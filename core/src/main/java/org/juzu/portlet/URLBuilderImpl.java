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

import javax.portlet.BaseURL;

import org.juzu.URLBuilder;

/**
 * Author : Nguyen Thanh Hai
 *          haithanh0809@gmail.com
 * Aug 1, 2012  
 */
public class URLBuilderImpl implements URLBuilder {

	private final BaseURL url;
	
	URLBuilderImpl(BaseURL url) {
		this.url = url;
	}
	
	public URLBuilder setParameter(String name, String value) {
		url.setParameter(name, value);
		return this;
	}
	
	@Override
	public String toString() {
		return url.toString();
	}
}
