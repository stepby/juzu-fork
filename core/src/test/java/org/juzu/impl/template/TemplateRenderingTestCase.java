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

import java.awt.AWTError;
import java.awt.AWTException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.util.Date;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.juzu.impl.spi.template.gtmpl.GroovyTemplate;
import org.juzu.text.WriterPrinter;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 * Apr 2, 2012
 */
public class TemplateRenderingTestCase extends AbstractTemplateTestCase {
	
	private DateFormat dateFormatFR;
	
	private DateFormat dateFormatEN;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dateFormatFR = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.FRANCE);
		dateFormatEN = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.ENGLISH);
	}
	
	public void testDate1() throws Exception {
		Date dateToTest = new Date(0);
		String template = "<%print(new Date(0));%>";
		assertEquals(dateFormatFR.format(dateToTest), render(template, Locale.FRANCE));
		assertEquals(dateFormatEN.format(dateToTest), render(template, Locale.ENGLISH));
		assertEquals(dateToTest.toString(), render(template));
	}
	
	public void testDate2() throws Exception {
		Date dateToTest = new Date(0);
		String template = "<%def date = new Date(0);%>${date}";
		assertEquals(dateFormatFR.format(dateToTest), render(template, Locale.FRANCE));
		assertEquals(dateFormatEN.format(dateToTest), render(template, Locale.ENGLISH));
		assertEquals(dateToTest.toString(), render(template));
	}
	
	public void testDate3() throws Exception {
		Date dateToTest = new Date(0);
		String template = "<%=new Date(0)%>";
		assertEquals(dateFormatFR.format(dateToTest), render(template, Locale.FRANCE));
		assertEquals(dateFormatEN.format(dateToTest), render(template, Locale.ENGLISH));
		assertEquals(dateToTest.toString(), render(template));
	}
	
	public void testFoo() throws Exception {
		String template = "a";
		String render = render(template);
		assertEquals("a", render);
	}
	
	public void testBar() throws Exception {
		String template = "<%='a'%>";
		String render = render(template);
		assertEquals("a", render);
	}
	
	public void testFooBar() throws Exception {
		String template = "a<%='b'%>c";
		String render = render(template);
		assertEquals("abc", render);
	}
	
	public void testJuu() throws Exception {
		String template = "<% out.print(\"a\") %>";
		String render = render(template);
		assertEquals("a", render);
	}
	
	public void testLineBreak() throws Exception {
		String template = "\n";
		String render = render(template);
		assertEquals("\n", render);
	}
	
	public void testMultiLine() throws Exception {
		assertEquals("a\nb\nc\nd", render("a\nb\n<%= 'c' %>\nd"));
		assertEquals("a\nb\n", render("a\n<% if(true) {\n%>b\n<% } %>"));
		assertEquals("a\nb", render("<% //foo%>a\nb"));
		assertEquals("ab", render("a<%\n%>b"));
	}
	
	public void testIf() throws Exception {
		String template = 
				"a\n" +
				"<% if (true) {\n %>" +
				"b\n" +
				"<% } %>";
		String render = render(template);
		assertEquals("a\nb\n", render);
	}
	
	public void testLineComment() throws Exception {
		String template = "<% //foo %>a\nb";
		String render = render(template);
		assertEquals("a\nb", render);
	}
	
	public void testSimple() throws Exception {
		assertEquals("a", render("a"));
		assertEquals("\n", render("\n"));
		assertEquals("a", render("<%='a'%>"));
		assertEquals("abc", render("a<%='b'%>c"));
		assertEquals("a", render("<% out.print(\"a\"); %>"));
	}
	
	public void testContextResolution() throws Exception {
		Map<String, String> context = new HashMap<String, String>();
		context.put("foo", "bar");
		assertEquals("bar", render("<%=foo%>", context));
	}
	
	public void testDollarInExpression() throws Exception {
		Map<String, String> context = new HashMap<String, String>();
		context.put("foo", "bar");
		assertEquals("bar", render("<%= \"$foo\"%>", context));
	}
	
	public void testEscapeDollarInExpression() throws Exception {
		Map<String, String> context = new HashMap<String, String>();
		context.put("foo", "bar");
		assertEquals("$foo", render("<%=\"\\$foo\"%>", context));
	}
	
	public void testEscapeDollarInText() throws Exception {
		Map<String, String> context = new HashMap<String, String>();
		context.put("foo", "bar");
		assertEquals("$foo", render("\\$foo", context));
	}
	
	public void testDollarInScriplet() throws Exception {
		Map<String,String> context = new HashMap<String,String>();
		context.put("foo", "bar");
		assertEquals("bar", render("<% out.print(\"$foo\")%>", context));
	}
	
	public void testEscapeDollarInScriplet() throws Exception {
		Map<String,String> context = new HashMap<String,String>();
		context.put("foo", "bar");
		assertEquals("$foo", render("<% out.print(\"\\$foo\") %>", context));
	}
	
	public void testQuote() throws Exception {
		assertEquals("\"", render("\""));
	}
	
	public void testNoArgUrl() throws Exception {
		String s = render("@{foo()}");
		assertEquals("foo_value", s);
	}
	
	public static String foo() {
		return "foo_value";
	}
	
	public static String echo(String s) {
		return s;
	}
	
	public void testException() throws Exception {
		try {
			render("<% throw new java.awt.AWTException(); %>");
			fail();
		} catch(TemplateExecutionException e) {
			assertTrue(e.getCause() instanceof AWTException);
		}
	}
	
	public void testRuntimeException() throws Exception {
		try {
			render("<% throw new java.util.EmptyStackException(); %>");
			fail();
		} catch(TemplateExecutionException e) {
			assertTrue(e.getCause() instanceof EmptyStackException);
		}
	}
	
	public void testIOException() throws Exception {
		try {
			render("<% throw new java.io.IOException(); %>");
			fail();
		} catch(IOException e) {
		}
	}
	
	public void testError() throws Exception {
		try {
			render("<% throw new java.awt.AWTError(); %>");
			fail();
		} catch(AWTError e){
		}
	}
	
	public void testThrowable() throws Exception {
		try {
			render("<% throw new Throwable(); %>");
			fail();
		} catch(Throwable e) {
		}
	}
	
	public void testScriptLineNumber() throws Exception {
		testLineNumber("<%");
		assertLineNumber(2, "throw new Exception('e')", "<%\nthrow new Exception('e')%>");
	}
	
	public void testExpressionLineNumber() throws Exception {
		testLineNumber("<%=");
	}
	
	private void testLineNumber(String prolog) throws Exception {
		assertLineNumber(1, "throw new Exception('a')", prolog + "throw new Exception('a')%>");
		assertLineNumber(1, "throw new Exception('b')", "foo" + prolog + "throw new Exception('b')%>");
		assertLineNumber(2, "throw new Exception('c')", "foo\n" + prolog + "throw new Exception('c')%>");
		assertLineNumber(1, "throw new Exception('d')", "<%;%>foo" + prolog + "throw new Exception('d')%>");
	}
	
	public static Object out;
	
	public void testWriteAccess() throws Exception {
		out = null;
		Writer writer = new StringWriter();
		GroovyTemplate template = template("<%" + TemplateRenderingTestCase.class.getName() + ".out = out; %>");
		template.render(new WriterPrinter(writer), null, null);
		assertNotNull(out);
	}
	
	private void assertLineNumber(int expectedLineNumber, String expectedText, String script) throws IOException {
		GroovyTemplate template = template(script);
		try {
			template.render(new WriterPrinter(new StringWriter()), null, null);
			fail();
		} catch (TemplateExecutionException t) {
			assertEquals(expectedText, t.getText());
			assertEquals(expectedLineNumber, t.getLine());
			StackTraceElement scriptElt = null;
			for(StackTraceElement elt : t.getCause().getStackTrace()) {
				if(elt.getClassName().equals(template.getClassName())) {
					scriptElt = elt;
					break;
				}
			}
			assertEquals(expectedLineNumber, scriptElt.getLineNumber());
		}
	}
}
