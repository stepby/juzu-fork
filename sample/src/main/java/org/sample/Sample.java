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
package org.sample;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.juzu.Action;
import org.juzu.Render;
import org.juzu.Resource;
import org.juzu.application.ApplicationDescriptor;
import org.juzu.application.RenderLiteral;
import org.juzu.template.Template;
import org.juzu.text.Printer;


/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public class Sample {

	static {
		ApplicationDescriptor desc = SampleApplication.DESCRIPTOR;
	}
	
	@Inject @Resource("MyTemplate.gtmpl")
	private Template template;
	
	@Inject
	Printer printer;
	
	@Action
	public RenderLiteral action() {
		return Sample_.render;
	}
	
	@Render
	public void render() throws IOException {
		//A generated template literal for MyTemplate
		org.sample.templates.MyTemplate literal;
		
		//Render template
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("action", "" + Sample_.actionURL());
		data.put("render", "" + Sample_.renderURL());
		template.render(printer, data);
	}
}
