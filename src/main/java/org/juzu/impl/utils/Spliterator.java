/*
 * Copyright (C) 2012 eXo Platform SAS.
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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public class Spliterator implements Iterator<String> {
	
	private final String s;
	
	private final char separator;
	
	private int from;
	
	private Integer to;
	
	public Spliterator(String s, char seperator) throws NullPointerException {
		if(s == null) throw new NullPointerException();
		this.s = s;
		this.separator = seperator;
		this.from = s.length() == 0 ? -1 : 0;
		this.to = null;
	}
	
	public static List<String> split(String s, char seperator) throws NullPointerException {
		LinkedList<String> list = new LinkedList<String>();
		Spliterator iterator = new Spliterator(s, seperator);
		while(iterator.hasNext()) {
			String next = iterator.next();
			list.add(next);
		}
		return list;
	}

	public boolean hasNext() {
		if(from == -1) {
			return false;
		} else { 
			if(to == null) {
				to = s.indexOf(separator, from);
			}
			return true;
		}
	}

	public String next() {
		if(hasNext()) {
			String next;
			if(to == -1) {
				next = s.substring(from);
				from = -1;
			} else {
				next = s.substring(from, to);
				from = to + 1;
			}
			to = null;
			return next;
		} else throw new NoSuchElementException();
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
}
