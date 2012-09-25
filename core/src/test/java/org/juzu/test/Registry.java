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
package org.juzu.test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public class Registry
{
	private static final Map<Object, Object> state = new HashMap<Object, Object>();
	
   public static <T> T get(Object key){
		if(key == null) throw new NullPointerException();
		@SuppressWarnings("unchecked")
		T t = (T)state.get(key);
		return t;
	}
   
   public static <T> void set(Object key, Object value) {
   	if(key == null) {
   		throw new NullPointerException();
   	} else if(value != null) {
   		state.put(key, value);
   	} else {
   		state.remove(key);
   	}
   }
   
   public static <T> T unset(Object key) {
   	if(key == null) throw new NullPointerException();
   	return (T) state.remove(key);
   }
   
   public void clear() {
   	state.clear();
   }
}
