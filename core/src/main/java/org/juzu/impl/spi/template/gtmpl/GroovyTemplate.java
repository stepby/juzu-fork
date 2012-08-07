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

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.juzu.impl.spi.template.TemplateStub;
import org.juzu.impl.template.ASTNode;
import org.juzu.impl.template.ASTNode.Text;
import org.juzu.impl.template.TemplateExecutionException;
import org.juzu.text.Printer;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 * Apr 2, 2012
 */
public abstract class GroovyTemplate extends TemplateStub {
	
	protected final String templateId;
	
	private Class<?> scriptClass;
	
	private HashMap<Integer, ASTNode.Text> locationTable;
	
	protected GroovyTemplate() {
		this.templateId = getClass().getName();
	}
	
	public GroovyTemplate(String templateId) {
		this.templateId = templateId;
		this.locationTable = null;
		this.scriptClass = null;
	}
	
	private Class<?> getScriptClass() {
		if(scriptClass == null) {
			CompilerConfiguration config = new CompilerConfiguration();
			config.setScriptBaseClass(BaseScript.class.getName());
			String script = getScript();
			GroovyCodeSource gcs = new GroovyCodeSource(new StringReader(script), "myscript", "/groovy/shell");
			GroovyClassLoader loader = new GroovyClassLoader(Thread.currentThread().getContextClassLoader(), config);
			try {
				scriptClass = loader.parseClass(gcs, false);
				Class<?> constants = scriptClass.getClassLoader().loadClass("Constants");
				locationTable = (HashMap<Integer, Text>)constants.getField("TABLE").get(null);
			} catch(Exception e) {
				e.printStackTrace();
				throw new UnsupportedOperationException("handle me gracefully");
			}
		}
		return scriptClass;
	}
	
	public abstract String getScript();
	
	public String getClassName() {
		return getScriptClass().getName();
	}

	@Override
	public void render(Printer printer, Map<String, ?> context, Locale locale) throws TemplateExecutionException, IOException {
		Class<?> scriptClass = getScriptClass();
		BaseScript script = (BaseScript) InvokerHelper.createScript(scriptClass, context != null ? new Binding(context) : new Binding());
		script.setPrinter(new GroovyPrinter(printer, locale));
		try {
			script.run();
		} catch(Exception e) {
			if(e instanceof IOException)
				throw (IOException) e;
			else
				throw buildRuntimeException(e);
		} catch(Throwable e) {
			if(e instanceof Error)
				throw ((Error) e);
			throw buildRuntimeException(e);
		}
	}
	
	private TemplateExecutionException buildRuntimeException(Throwable t) {
		StackTraceElement[] trace = t.getStackTrace();
		ASTNode.Text firstItem = null;
		for(int i = 0; i < trace.length; i++) {
			StackTraceElement element = trace[i];
			if(element.getClassName().equals(scriptClass.getName())) {
				int lineNumber = element.getLineNumber();
				ASTNode.Text item = locationTable.get(lineNumber);
				int templateLineNumber;
				if(item != null) {
					templateLineNumber = item.getBeginPosition().getLine();
					if(firstItem == null) firstItem = item;
					else templateLineNumber = -1;
					
					element = new StackTraceElement(
						element.getClassName(),
						element.getMethodName(), 
						element.getFileName(), 
						templateLineNumber);
					trace[i] = element;
				}
			}
		}
		
		t.setStackTrace(trace);
		if(firstItem != null)
			return new TemplateExecutionException(templateId, firstItem.getBeginPosition(), firstItem.getData(), t);
		else
			return new TemplateExecutionException(templateId, null, null, t);
	}
}
