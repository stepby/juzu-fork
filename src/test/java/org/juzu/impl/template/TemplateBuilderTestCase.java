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
package org.juzu.impl.template;

import java.io.StringWriter;
import java.util.Collections;
import java.util.Random;

import org.juzu.impl.template.groovy.GroovyTemplate;
import org.juzu.impl.template.groovy.GroovyTemplateBuilder;
import org.juzu.text.WriterPrinter;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 * Apr 2, 2012
 */
public class TemplateBuilderTestCase extends TestCase {

	public void testFoo() throws Exception {
		TemplateParser parser = new TemplateParser();
		GroovyTemplateBuilder templateBuilder = new GroovyTemplateBuilder("template_" + Math.abs(new Random().nextLong()));
		parser.parse("a<%=foo%>c").build(templateBuilder);
		GroovyTemplate template = templateBuilder.build();
		StringWriter out = new StringWriter();
		template.render(new WriterPrinter(out), Collections.<String, Object>singletonMap("foo", "b"), null);
		assertEquals("abc", out.toString());
	}
}
