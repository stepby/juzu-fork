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
import groovy.lang.Script;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 * Mar 29, 2012
 */
public abstract class BaseScript extends Script {

	private GroovyPrinter printer;
	
	protected BaseScript() {
		
	}
	
	protected BaseScript(Binding binding) {
		super(binding);
	}
	
	public GroovyPrinter getPrinter() {
		return printer;
	}
	
	public void setPrinter(GroovyPrinter printer) {
		this.printer = printer;
	}
	
	public Object getProperty(String property) {
		if("out".equals(property)) return printer;
		else return super.getProperty(property);
	}
	
	@Override
	public void println() {
		printer.println();
	}
	
	@Override
	public void println(Object o) {
		printer.println(o);
	}
	
	@Override
	public void print(Object o) {
		printer.print(o);
	}
}
