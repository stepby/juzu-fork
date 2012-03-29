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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.juzu.impl.utils.CharSequenceReader;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 * Mar 28, 2012
 */
public class TemplateParser {

	private enum Status {
		TEXT,
		
		EXPR,
		
		SCRIPTLET,
		
		START_ANGLE,
		
		SCRIPTLET_OR_EXPR,
		
		MAYBE_SCRIPTLET_END,
		
		MAYBE_EXPR_END,
		
		MAYBE_GSTRING_EXPR,
		
		GSTRING_CURLY_EXPR,
		
		GSTRING_EXPR,
		
		BACKSLASH
	}
	
	public ASTNode.Template parse(CharSequenceReader reader) throws IOException {
		List<ASTNode.Section> sections = new ArrayList<ASTNode.Section>();
		StringBuilder accumulator = new StringBuilder();
		
		//
		int lineNumber = 1;
		int colNumber = 1;
		Location pos = new Location(lineNumber, colNumber);
		Status status = Status.TEXT;
		int i;
		while((i = reader.read()) != -1) {
			char c = (char)i;
			
			//On Windows, '\n\r' is new line
			if(c == '\r') {
				int j = reader.read();
				if(j != -1) {
					char c2 = (char)j;
					if(c2 == '\n') 
						c = '\n';
					else
						reader.unread(j);
				}
			}
			
			//Update current position
			if(c == '\n') {
				colNumber = 1;
				lineNumber++;
			} else {
				colNumber++;
			}
			
			//
			switch (status) {
				case TEXT :
					if(c == '<') 
						status = Status.START_ANGLE;
					else if(c == '\\')
						status = Status.BACKSLASH;
					else if(c == '$')
						status = Status.MAYBE_GSTRING_EXPR;
					else
						accumulator.append(c);
					break;
				case EXPR:
					if(c == '%')
						status = Status.MAYBE_EXPR_END;
					else
						accumulator.append(c);
					break;
				case SCRIPTLET:
					if(c == '%')
						status = Status.MAYBE_SCRIPTLET_END;
					else 
						accumulator.append(c);
					break;
				case START_ANGLE:
					if(c == '%')
						status = Status.SCRIPTLET_OR_EXPR;
					else {
						status = Status.TEXT;
						accumulator.append('<').append(c);
					}
					break;
				case SCRIPTLET_OR_EXPR:
					if(accumulator.length() > 0) {
						sections.add(new ASTNode.Section(SectionType.STRING, accumulator.toString(), pos));
						accumulator.setLength(0);
						pos = new Location(colNumber, lineNumber);
					}
					if(c == '=') 
						status = Status.EXPR;
					else if(c == '%')
						status = Status.MAYBE_SCRIPTLET_END;
					else {
						status = Status.SCRIPTLET;
						accumulator.append(c);
					}
					break;
				case MAYBE_SCRIPTLET_END:
					if(c == '>') {
						sections.add(new ASTNode.Section(SectionType.SCRIPTLET, accumulator.toString(), pos));
						pos = new Location(colNumber, lineNumber);
						accumulator.setLength(0);
						status = Status.TEXT;
					} else if(c == '%') {
						accumulator.append('%');
					} else {
						status = Status.SCRIPTLET;
						accumulator.append('%').append(c);
					}
					break;
				case MAYBE_EXPR_END:
					if(c == '>') {
						sections.add(new ASTNode.Section(SectionType.EXPR, accumulator.toString(), pos));
						pos = new Location(colNumber, lineNumber);
						accumulator.setLength(0);
						status = Status.TEXT;
					} else if(c == '%') {
						accumulator.append('%');
					} else {
						status = Status.EXPR;
						accumulator.append('%').append(c);
					}
					break;
				case MAYBE_GSTRING_EXPR:
					if(c == '{') {
						if(accumulator.length() > 0) {
							sections.add(new ASTNode.Section(SectionType.STRING, accumulator.toString(), pos));
							accumulator.setLength(0);
							pos = new Location(colNumber, lineNumber);
						}
						status = Status.GSTRING_CURLY_EXPR;
					} else if(Character.isJavaIdentifierStart(c)) {
						if(accumulator.length() > 0) {
							sections.add(new ASTNode.Section(SectionType.STRING, accumulator.toString(), pos));
							accumulator.setLength(0);
							pos = new Location(colNumber, lineNumber); 
						}
						status =  Status.GSTRING_EXPR;
						accumulator.append(c);
					} else {
						accumulator.append('$').append(c);
					}
					break;
				case GSTRING_CURLY_EXPR:
					if(c == '}') {
						sections.add(new ASTNode.Section(SectionType.EXPR, accumulator.toString(), pos));
						pos = new Location(colNumber, lineNumber);
						accumulator.setLength(0);
						status = Status.TEXT;
					} else {
						accumulator.append(c);
					}
					break;
				case GSTRING_EXPR:
					if(c == '.' || Character.isJavaIdentifierPart(c)) {
						accumulator.append(c);
					} else {
						sections.add(new ASTNode.Section(SectionType.EXPR, accumulator.toString(), pos));
						pos = new Location(colNumber, lineNumber);
						accumulator.setLength(0);
						status = Status.TEXT;
						accumulator.append(c);
					}
					break;
				case BACKSLASH:
					accumulator.append("\\");
					accumulator.append(c);
					status = Status.TEXT;
					break;
				default :
					throw new AssertionError();
			}
		}
		
		//Last section
		if(accumulator.length() > 0) {
			switch (status) {
				case TEXT :
					sections.add(new ASTNode.Section(SectionType.STRING, accumulator.toString(), pos));
					break;
				case GSTRING_EXPR:
					sections.add(new ASTNode.Section(SectionType.EXPR, accumulator.toString(), pos));
					break;
				default :
					throw new AssertionError();
			}
		}
		//
		return new ASTNode.Template(Collections.unmodifiableList(sections));
	}
	
	public ASTNode.Template parse(CharSequence s) {
		try {
			return parse(new CharSequenceReader(s));
		} catch(IOException e) {
			throw new UnsupportedOperationException(e);
		}
	}
}
