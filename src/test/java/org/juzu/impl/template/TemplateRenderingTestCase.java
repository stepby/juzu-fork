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

import org.juzu.impl.template.groovy.GroovyTemplate;
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
		String template = "<%def date = new Date(0);%>$date";
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
	
	public void testSimple() throws Exception {
		assertEquals("a", render("a"));
		assertEquals("\n", render("\n"));
		assertEquals("a", render("<%='a'%>"));
		assertEquals("abc", render("a<%='b'%>c"));
		assertEquals("a", render("<% out.print(\"a\"); %>"));
	}
	
	public void testMultiLine() throws Exception {
		assertEquals("a\nb\nc\nd", render("a\nb\n<%= 'c' %>\nd"));
		assertEquals("a\nb\n", render("a\n<% if(true) {\n%>b\n<% } %>"));
		assertEquals("a\nb", render("<% //foo%>a\nb"));
	}
	
	public void testContextResolution() throws Exception {
		Map<String, String> context = new HashMap<String, String>();
		context.put("foo", "bar");
		assertEquals("bar", render("<%=foo%>", context));
	}
	
	public void testGString() throws Exception {
		Map<String, String> context = new HashMap<String, String>();
		context.put("foo", "bar");
		assertEquals("bar", render("$foo", context));
		assertEquals("bar", render("${foo}", context));
	}
	
	public void testQuoteAfterGString() throws Exception {
		Map<String, String> context = new HashMap<String, String>();
		context.put("foo", "bar");
		assertEquals("bar\"", render("$foo\"", context));
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
	
	public static Object out;
	
	public void testWriteAccess() throws Exception {
		out = null;
		Writer writer = new StringWriter();
		GroovyTemplate template = template("<%" + TemplateRenderingTestCase.class.getName() + ".out = out; %>");
		template.render(new WriterPrinter(writer), null, null);
		assertNotNull(out);
	}
}
