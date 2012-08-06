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
package org.juzu.impl.utils;

import java.util.HashMap;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public class Builder {
	
	public static <K, V> Map<K, V> map(K key, V value) {
		return new Map<K, V>().put(key, value);
	}

	public static class Map<K, V> {
		
		private java.util.Map<K, V> map;
		
		public Map() {
			this.map = new HashMap<K, V>();
		}
		
		public Map<K, V> put(K key, V value) {
			map.put(key, value);
			return this;
		}
		
		public java.util.Map<K, V> build() {
			return map;
		}
	}
}
