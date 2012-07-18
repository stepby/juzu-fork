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

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.juzu.text.CharArray;
import org.juzu.text.Printer;

import groovy.lang.GString;
import groovy.lang.GroovyInterceptable;
import groovy.lang.GroovyObjectSupport;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 * Mar 29, 2012
 */
public class GroovyPrinter extends GroovyObjectSupport implements GroovyInterceptable {

	private final Printer printer;
	
	private final Locale locale;
	
	public GroovyPrinter(Printer printer, Locale locale) {
		if(printer == null) throw new NullPointerException("No null printer accepted");
		this.printer = printer;
		this.locale = locale;
	}
	
	public GroovyPrinter(Printer printer) {
		this(printer, null);
	}
	
	public Locale getLocale() {
		return locale;
	}
	
	/*
	 * Optimize the call to the various print methods.
	 *  
	 * */
	@Override
	public Object invokeMethod(String name, Object args) {
		//Optimize access to print method
		if(args instanceof Object[]) {
			Object[] array = (Object[])args;
			if(array.length == 1) {
				if("print".equals(name)) {
					print(array[0]);
					return null;
				} else if("println".equals(name)) {
					println(array[0]);
					return null;
				}
			}
		}
		return super.invokeMethod(name, args);
	}
	
	private Object format(Object o) {
		if(o instanceof Date) {
			if(locale != null) {
				DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
				o = dateFormat.format((Date)o);
			}
		}
		return o;
	}
	
	private String toString(Object o) {
		Object f = format(o);
		if(f == null) return "null";
		else if(f instanceof String) return (String) f;
		else return f.toString();
	}
	
	public final void println() {
		try {
			printer.write('\n');
		} catch(IOException ignore) { }
	}
	
	public final void println(Object o) {
		print(o);
		println();
	}
	
	public final void print(Object o) {
		try {
			if(o instanceof CharArray) {
				printer.write((CharArray)o);
			} else if(o instanceof GString) {
				GString gs = (GString)o;
				Object[] values = gs.getValues();
				for(int i = 0; i < values.length; i++) {
					values[i] = format(values[i]);
				}
				printer.write(o.toString());
			} else {
				printer.write(toString(o));
			}
		} catch(IOException ignore) { }
	}
}
