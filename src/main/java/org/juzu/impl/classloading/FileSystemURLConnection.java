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
package org.juzu.impl.classloading;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.juzu.impl.utils.Content;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public class FileSystemURLConnection extends URLConnection {
	
	private final Content<?> content;

	protected FileSystemURLConnection(URL url, Content<?> content) {
		super(url);
		this.content = content;
	}

	@Override
	public void connect() throws IOException {
		
	}
	
	@Override
	public InputStream getInputStream() throws IOException {
		return content.getInputStream();
	}
	
	@Override
	public long getLastModified() {
		return content.getLastModified();
	}
}
