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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.juzu.impl.template.ASTNode;
import org.juzu.impl.template.SectionType;
import org.juzu.utils.Location;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 * Mar 28, 2012
 */
public class TemplateParserTestCase extends TestCase {
	
	private ASTBuilder parser = new ASTBuilder();
	
	public void testEmpty() {
		assertEquals(Collections.emptyList(), parser.parse("").getSections());
	}
	
	public void testText() {
		List<ASTNode.Section> expected = Arrays.asList(
			new ASTNode.Section(SectionType.STRING, "bar\nfoo")
		);
		assertEquals(expected, parser.parse("bar\nfoo").getSections());
	}
	
	public void testSingleEmptyScriptlet() {
		assertEquals(Arrays.asList(new ASTNode.Section(SectionType.SCRIPTLET,"")), parser.parse("<%%>").getSections());
	}
	
	public void testSingleEmptyExpression() {
		assertEquals(Arrays.asList(new ASTNode.Section(SectionType.EXPR,"")), parser.parse("<%=%>").getSections());
	}
	
	public void testSingleScriptlet() {
		assertEquals(Arrays.asList(new ASTNode.Section(SectionType.SCRIPTLET,"a")), parser.parse("<%a%>").getSections());
	}
	
	public void testSingleExpression() {
		assertEquals(Arrays.asList(new ASTNode.Section(SectionType.EXPR, "a")), parser.parse("<%=a%>").getSections());
	}

	public void testPercentScriptlet() {
		assertEquals(Arrays.asList(new ASTNode.Section(SectionType.SCRIPTLET, "%")), parser.parse("<%%%>").getSections());
	}
	
	public void testPercentExpression() {
		assertEquals(Arrays.asList(new ASTNode.Section(SectionType.EXPR, "%")), parser.parse("<%=%%>").getSections());
	}
	
	public void testAngleBracketScriptlet() {
		assertEquals(Arrays.asList(new ASTNode.Section(SectionType.SCRIPTLET, "<")), parser.parse("<%<%>").getSections());
		assertEquals(Arrays.asList(new ASTNode.Section(SectionType.SCRIPTLET, ">")), parser.parse("<%>%>").getSections());
	}
	
	public void testAngleBracketExpression() {
		assertEquals(Arrays.asList(new ASTNode.Section(SectionType.EXPR, "<")), parser.parse("<%=<%>").getSections());
	}
	
	public void testCurlyExpression() {
		assertEquals(Arrays.<ASTNode.Section>asList(new ASTNode.Section(SectionType.EXPR, "a")), parser.parse("${a}").getSections());
	}
	
	public void testSimpleScript1() {
		assertEquals(Arrays.asList(
			new ASTNode.Section(SectionType.STRING, "a"),
			new ASTNode.Section(SectionType.SCRIPTLET, "b"),
			new ASTNode.Section(SectionType.STRING, "c")),
			parser.parse("a<%b%>c").getSections()
		);
	}
	
	public void testSimpleScript2() {
		assertEquals(Arrays.asList(
			new ASTNode.Section(SectionType.STRING, "a"),
			new ASTNode.Section(SectionType.EXPR, "b"),
			new ASTNode.Section(SectionType.STRING, "c")),
			parser.parse("a<%=b%>c").getSections()
		);
	}
	
	public void testPosition() {
		List<ASTNode.Section> list = parser.parse("a\nb<%= foo %>c").getSections();
		assertEquals(new Location(1,1), list.get(0).getItems().get(0).getBeginPosition());
		assertEquals(new Location(2,1), list.get(0).getItems().get(1).getBeginPosition());
		assertEquals(new Location(1,2), list.get(0).getItems().get(2).getBeginPosition());
		assertEquals(new Location(2,2), list.get(1).getItems().get(0).getBeginPosition());
		assertEquals(new Location(11,2), list.get(2).getItems().get(0).getBeginPosition());
	}
}
