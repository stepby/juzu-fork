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
import java.util.Iterator;

import org.juzu.impl.utils.Content;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 * Mar 16, 2012
 */
public abstract class FileSystem<P> {
	
	public final StringBuilder packageName(P path) throws IOException {
		if(isDir(path)) {
			P parent = getParent(path);
			if(parent == null) {
				return new StringBuilder();
			} else {
				StringBuilder sb = packageName(parent);
				String name = getName(path);
				if(sb.length() > 0) {
					sb.append('.');
				}
				sb.append(name);
				return sb;
			}
		} else {
			return packageName(getParent(path));
		}
	}
	
	public abstract boolean equals(P left, P right);
	
	public abstract P getRoot() throws IOException;
	
	public abstract P getParent(P path) throws IOException;
	
	public abstract String getName(P path) throws IOException;
	
	public abstract Iterator<P> getChildren(P dir) throws IOException;
	
	public abstract P getChild(P dir, String name) throws IOException;
	
	public abstract boolean isDir(P path) throws IOException;
	
	public abstract boolean isFile(P path) throws IOException;
	
	public abstract Content<?> getContent(P file) throws IOException;
	
	public abstract long getLastModified(P path) throws IOException;
}
