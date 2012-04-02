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
package org.juzu.impl.template.groovy;

import org.juzu.text.CharArray;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 * Apr 2, 2012
 */
class TextConstant {
	
	final String name;
	
	final String text;
	
	TextConstant(String name, String text) {
		this.name = name; 
		this.text = text;
	}
	
	String getDeclaration() {
		StringBuilder builder = new StringBuilder("");
		for(int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if(c == '\n')
				builder.append("\\n");
			else if(c == '\"')
				builder.append("\\\"");
			else
				builder.append(c);
		}
		return "public static final " + CharArray.Simple.class.getName() + " " + name + " = new " + CharArray.Simple.class.getName() + "(\"" + builder + "\");";
	}
}
