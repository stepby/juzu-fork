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
package org.juzu.impl.spi.fs.ram;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import org.juzu.impl.utils.Content;
import org.juzu.impl.utils.Spliterator;

import sun.net.www.ParseUtil;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
class RAMURLStreamHandler extends URLStreamHandler {

	private RAMFileSystem fs;
	
	public RAMURLStreamHandler(RAMFileSystem fs) {
		this.fs = fs;
	}
	
	@Override
	protected URLConnection openConnection(URL u) throws IOException {
		Iterable<String> names = Spliterator.split(u.getPath().substring(1), '/');
		RAMPath path = fs.getPath(names);
		if(path != null && fs.isFile(path)) {
			Content<?> content = fs.getContent(path);
//			char[] chars = content.getCharSequence().toString().toCharArray();
//			for(int i = 0; i < chars.length; i++) {
//				System.out.println("index = " + i + ", char =[" + chars[i] + "]");
//			}
			if(content != null) {
				return new RAMURLConnection(u, content);
			}
		}
		throw new IOException("Could not connect non existing content " + names);
	}
}
