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

import org.json.JSONException;
import org.json.JSONObject;
import org.juzu.URLBuilder;
import org.juzu.application.Phase;
import org.juzu.test.AbstractTestCase;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public class MockURLBuilder implements URLBuilder
{
	private final Phase phase;
	
	private final Map<Object, Object> parameters;
	
	public MockURLBuilder(Phase phase) {
		this.phase = phase;
		this.parameters = new HashMap<Object, Object>();
	}

   public URLBuilder setParameter(String name, String value)
   {
	   if(name == null || value == null) throw new NullPointerException();
	   parameters.put(name, value);
	   return this;
   }
   
   @Override
   public String toString() {
   	try
      {
   		JSONObject url = new JSONObject();
	      url.put("phase", phase);
	      url.put("parameters", parameters);
	      return url.toString();
      }
      catch (JSONException e)
      {
	      throw AbstractTestCase.failure(e);
      }
   }
}
