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
package org.juzu.impl.spi.template.gtmpl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public abstract class GroovyTemplateLiteral extends GroovyTemplate {
	
	public GroovyTemplateLiteral() {
	}

	@Override
	public final String getScript() {
		try {
			String path = templateId.replace('.', '/')  + ".groovy";
			URL url = getClass().getClassLoader().getResource(path);
			if(url != null) {
				byte[] buffer = new byte[256];
				InputStream in = url.openStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				for(int l = in.read(buffer); l != -1; l = in.read(buffer))	 {
					baos.write(buffer,0 ,l);
				}
				return baos.toString();
			} else {
				System.out.println("Could not load resource " + path);
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
