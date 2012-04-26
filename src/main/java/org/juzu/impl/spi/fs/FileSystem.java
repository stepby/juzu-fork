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

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 * Mar 16, 2012
 */
public interface FileSystem<P, D extends P, F extends P> {
	
	boolean equals(P left, P right);
	
	D getRoot() throws IOException;
	
	D getParent(P path) throws IOException;
	
	String getName(P path) throws IOException;
	
	Iterator<P> getChildren(D dir) throws IOException;
	
	P getChild(D dir, String name) throws IOException;
	
	boolean isDir(P path) throws IOException;
	
	boolean isFile(P path) throws IOException;
	
	F asFile(P path) throws IllegalArgumentException, IOException;
	
	D asDir(P path) throws IllegalArgumentException, IOException;
	
	Content getContent(F file) throws IOException;
	
	long getLastModified(P path) throws IOException;
}
