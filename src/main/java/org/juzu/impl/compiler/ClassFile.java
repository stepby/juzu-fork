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
package org.juzu.impl.compiler;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 * Mar 16, 2012
 */
public class ClassFile {

	private final String fqn;
	
	private final byte[] bytes;
	
	private final long lastModified;
	
	public ClassFile(String fqn, byte[] bytes) {
		this.fqn = fqn;
		this.bytes = bytes;
		this.lastModified = System.currentTimeMillis();
	}
	
	public long getLastModified() {
		return lastModified;
	}
	
	public byte[] getBytes() {
		return bytes;
	}
	
	public String getFQN() {
		return fqn;
	}
}
