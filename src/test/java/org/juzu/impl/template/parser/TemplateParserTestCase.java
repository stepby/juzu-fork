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
package org.juzu.impl.template.parser;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 * Mar 28, 2012
 */
public class TemplateParserTestCase extends TestCase {
	
	private TemplateParser parser = new TemplateParser();
	
	public void testEmpty() {
		assertEquals(Collections.emptyList(), parser.parse(""));
	}
	
	public void testText() {
		List<TemplateSection> expected = Arrays.asList(
			new TemplateSection(SectionType.STRING, "bar\nfoo")
		);
		assertEquals(expected, parser.parse("bar\nfoo"));
	}
	
	public void testSingleEmptyScriptlet() {
		assertEquals(Arrays.asList(new TemplateSection(SectionType.SCRIPTLET,"")), parser.parse("<%%>"));
	}
	
	public void testSingleEmptyExpression() {
		assertEquals(Arrays.asList(new TemplateSection(SectionType.EXPR,"")), parser.parse("<%=%>"));
	}
	
	public void testSingleScriptlet() {
		assertEquals(Arrays.asList(new TemplateSection(SectionType.SCRIPTLET,"a")), parser.parse("<%a%>"));
	}
	
	public void testSingleExpression() {
		assertEquals(Arrays.asList(new TemplateSection(SectionType.EXPR, "a")), parser.parse("<%=a%>"));
	}

	public void testPercentScriptlet() {
		assertEquals(Arrays.asList(new TemplateSection(SectionType.SCRIPTLET, "%")), parser.parse("<%%%>"));
	}
	
	public void testPercentExpression() {
		assertEquals(Arrays.asList(new TemplateSection(SectionType.EXPR, "%")), parser.parse("<%=%%>"));
	}
	
	public void testAngleBracketScriptlet() {
		assertEquals(Arrays.asList(new TemplateSection(SectionType.SCRIPTLET, "<")), parser.parse("<%<%>"));
		assertEquals(Arrays.asList(new TemplateSection(SectionType.SCRIPTLET, ">")), parser.parse("<%>%>"));
	}
	
	public void testAngleBracketExpression() {
		assertEquals(Arrays.asList(new TemplateSection(SectionType.EXPR, "<")), parser.parse("<%=<%>"));
	}
	
	public void testSimpleScript1() {
		assertEquals(Arrays.asList(
			new TemplateSection(SectionType.STRING, "a"),
			new TemplateSection(SectionType.SCRIPTLET, "b"),
			new TemplateSection(SectionType.STRING, "c")),
			parser.parse("a<%b%>c")
		);
	}
	
	public void testSimpleScript2() {
		assertEquals(Arrays.asList(
			new TemplateSection(SectionType.STRING, "a"),
			new TemplateSection(SectionType.EXPR, "b"),
			new TemplateSection(SectionType.STRING, "c")),
			parser.parse("a<%=b%>c")
		);
	}
	
	public void testPosition() {
		List<TemplateSection> list = parser.parse("a\nb<%= foo %>c");
		assertEquals(new Location(1,1), list.get(0).getItems().get(0).getPosition());
		assertEquals(new Location(2,1), list.get(0).getItems().get(1).getPosition());
		assertEquals(new Location(1,2), list.get(0).getItems().get(2).getPosition());
		assertEquals(new Location(5,2), list.get(1).getItems().get(0).getPosition());
		assertEquals(new Location(12,2), list.get(2).getItems().get(0).getPosition());
	}
}
