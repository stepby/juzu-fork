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
package org.juzu.impl.spi.fs.war;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.portlet.PortletContext;
import javax.servlet.ServletContext;

import org.juzu.impl.spi.fs.ReadFileSystem;
import org.juzu.impl.utils.Content;
import org.juzu.impl.utils.Safe;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public abstract class WarFileSystem extends ReadFileSystem<String> {

	private final String mountPoint;
	
	public WarFileSystem(String mountPoint) throws NullPointerException {
		if(mountPoint == null) throw new NullPointerException("No null mount point accepted");
		if(!mountPoint.startsWith("/") || !mountPoint.endsWith("/")) throw new IllegalArgumentException("Invalid mount point " + mountPoint);
		
		//
		this.mountPoint = mountPoint.substring(0, mountPoint.length() - 1);
	}
	
	@Override
	public boolean equals(String left, String right) {
		return left.equals(right);
	}

	@Override
	public String getRoot() throws IOException {
		return "/";
	}

	@Override
	public String getParent(String path) throws IOException {
		//It's a directory, remove the trailing '/'
		if(path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}

		//Get the index of last '/'
		int index = path.lastIndexOf('/');
		
		//
		if(index == -1) return null;
		else return path.substring(0, index + 1);
	}

	@Override
	public String getName(String path) throws IOException {
		//It's a directory, remove the trailing '/'
		if(path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		
		//Get the index of last '/'
		int index = path.lastIndexOf('/');
		
		//return name
		return path.substring(index + 1);
	}

	@Override
	public Iterator<String> getChildren(String dir) throws IOException {
		return getResourcePaths(dir).iterator();
	}

	@Override
	public String getChild(String dir, String name) throws IOException {
		for(Iterator<String> i = getChildren(dir); i.hasNext();) {
			String child = i.next();
			String childName = getName(child);
			if(childName.equals(name)) return child;
		}
		return null;
	}

	@Override
	public boolean isDir(String path) throws IOException {
		return path.endsWith("/");
	}

	@Override
	public boolean isFile(String path) throws IOException {
		return !isDir(path);
	}

	@Override
	public Content<?> getContent(String file) throws IOException {
		URL url = getResource(file);
		if(url != null) {
			URLConnection conn = url.openConnection();
			long lastModified = conn.getLastModified();
			InputStream is = conn.getInputStream();
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buffer = new byte[256];
				BufferedInputStream bis = new BufferedInputStream(is);
				for(int l = bis.read(buffer); l != -1; l = bis.read(buffer)) {
					baos.write(buffer, 0, l);
				}
				return new Content.ByteArray(lastModified, baos.toByteArray());
			} finally {
				Safe.close(is);
			}
		}
		return null;
	}

	@Override
	public long getLastModified(String path) throws IOException {
		URL url = getResource(path);
		URLConnection conn = url.openConnection();
		return conn.getLastModified();
	}

	@Override
	public URL getURL(String path) throws IOException {
		return getResource(path);
	}
	
	protected abstract Set<String> doGetResourcePaths(String path) throws IOException;
	
	protected abstract URL doGetResource(String path) throws IOException;
	
	private Collection<String> getResourcePaths(String path) throws IOException {
		Set<String> resourcePaths = doGetResourcePaths(mountPoint + path);
		if(resourcePaths != null) {
			List<String> tmp = new ArrayList<String>(resourcePaths.size());
			for(String resourcePath : resourcePaths) {
				tmp.add(resourcePath);
			}
			return tmp;
		} else return Collections.emptyList();
	}
	private URL getResource(String path) throws IOException {
		return doGetResource(mountPoint + path); 
	}
	
	public static WarFileSystem create(ServletContext servletContext) {
		return create(servletContext, "/");
	}
	
	public static WarFileSystem create(final ServletContext servletContext, String moutPoint) {
		return new WarFileSystem(moutPoint) {
			@Override
			protected Set<String> doGetResourcePaths(String path) throws IOException {
				return servletContext.getResourcePaths(path);
			}
			@Override
			protected URL doGetResource(String path) throws IOException {
				return servletContext.getResource(path);
			}
		};
	}
	
	public static WarFileSystem create(final PortletContext portletContext) {
		return create(portletContext, "/");
	}
	
	public static WarFileSystem create(final PortletContext portletContext, String mountPoint) {
		return new WarFileSystem(mountPoint) {
			@Override
			protected Set<String> doGetResourcePaths(String path) throws IOException {
				return portletContext.getResourcePaths(path);
			}
			@Override
			protected URL doGetResource(String path) throws IOException {
				return portletContext.getResource(path);
			}
		};
	}
}
