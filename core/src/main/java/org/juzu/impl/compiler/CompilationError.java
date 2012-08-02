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

import java.io.File;

import org.juzu.utils.Location;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public class CompilationError {

	private final String source;
	
	private final File sourceFile;
	
	private final Location location;
	
	private final String message;
	
	public CompilationError(String source, File sourceFile, Location location, String message) {
		this.source = source;
		this.sourceFile = sourceFile;
		this.location = location;
		this.message = message;
	}

	public String getSource() {
		return source;
	}

	public File getSourceFile() {
		return sourceFile;
	}

	public Location getLocation() {
		return location;
	}

	public String getMessage() {
		return message;
	}
	
	@Override
	public String toString() {
		return "CompilationError[source=" + source + ", message=" + message + ", location=" + location + "]"; 
	}
}
