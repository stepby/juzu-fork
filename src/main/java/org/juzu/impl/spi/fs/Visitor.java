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
package org.juzu.impl.spi.fs;

import java.io.IOException;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public interface Visitor<P> {
	
	boolean enterDir(P dir, String name) throws IOException;
	
	void file(P file, String name) throws IOException;
	
	void leaveDir(P dir, String name) throws IOException;
	
	/**
	 * A default implementation for Visitor
	 */
	public static class Default<P> implements Visitor<P> {
		public boolean enterDir(P dir, String name) throws IOException {
			return false;
		}

		public void file(P file, String name) throws IOException {
		}

		public void leaveDir(P dir, String name) throws IOException {
		}
	}
}
