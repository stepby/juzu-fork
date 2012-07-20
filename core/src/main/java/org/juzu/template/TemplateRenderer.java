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
package org.juzu.template;

import java.io.IOException;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.juzu.impl.spi.template.TemplateStub;
import org.juzu.impl.template.TemplateExecutionException;
import org.juzu.text.Printer;
import org.juzu.text.WriterPrinter;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public class TemplateRenderer {

	private final String templateId;
	
	private TemplateStub stub;
	
	public TemplateRenderer(String templateId) {
		this.stub = null;
		this.templateId = templateId;
	}
	
	public void render() throws TemplateExecutionException, IOException {
		render(Collections.<String, Object>emptyMap(), null);
	}
	
	public void render(Locale locale) throws TemplateExecutionException, IOException {
		render(Collections.<String, Object>emptyMap(), locale);
	}
	
	public void render(Map<String, ?> context) throws TemplateExecutionException, IOException {
		render(context, null);
	}
	
	public void render(Map<String, ?> context, Locale locale) throws TemplateExecutionException, IOException {
		if(stub == null) {
			try {
				ClassLoader cl = Thread.currentThread().getContextClassLoader();
				Class<?> stubClass = cl.loadClass(templateId);
				stub = (TemplateStub)stubClass.newInstance();
			} catch(Exception e) {
				throw new UnsupportedOperationException("handle me gracefully");
			}
		}
		
		Printer printer = new WriterPrinter(System.out);
		stub.render(printer, context, locale);
	}
	
	@Override
	public String toString() {
		return "TemplateRenderer[" + templateId + "]";
	}
}
