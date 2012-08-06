/*
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.juzu.impl.template;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.io.input.CharSequenceReader;
import org.juzu.utils.Location;


/**
 * Author : Nguyen Thanh Hai
 *          haithanh0809@gmail.com
 * Aug 7, 2012  
 */
public class ASTBuilder {

	public ASTNode.Template parse(CharSequence s) {
		return build(new CharSequenceReader(s));
	}
	
	private ASTNode.Template build(CharSequenceReader reader) {
		ArrayList<ASTNode.Section> sections = new ArrayList<ASTNode.Section>();
		TemplateParser simple = new TemplateParser(reader);
		
		Token token;
		try {
			token = simple.parse();
		} catch(ParseException e) {
			throw new AssertionError(e);
		}
		
		//
		StringBuilder accumulator = new StringBuilder();
		Location pos = null;
		for(;token != null; token = token.next) {
			switch (token.kind) {
				//
				case TemplateParserConstants.EOF:
					break;
			
				//
				case TemplateParserConstants.DATA:
					if(pos == null) pos = new Location(token.beginColumn, token.beginLine);
					accumulator.append(token.image.charAt(0));
					break;
					
				//
				case TemplateParserConstants.OPEN_EXPR:
				case TemplateParserConstants.OPEN_CURLY_EXPR:
				case TemplateParserConstants.OPEN_SCRIPTLET:
					if(accumulator.length() > 0) {
						sections.add(new ASTNode.Section(SectionType.STRING, accumulator.toString(), pos));
						accumulator.setLength(0);
					}
					pos = new Location(token.beginColumn, token.beginLine);
					break;
					
				//
				case TemplateParserConstants.EXPR_DATA:
				case TemplateParserConstants.CURLY_EXPR_DATA:
				case TemplateParserConstants.SCRIPTLET_DATA:	
					accumulator.append(token.image.charAt(0));
					break;
					
				//		
				case TemplateParserConstants.CLOSE_EXPR:
				case TemplateParserConstants.CLOSE_CURLY_EXPR:
					sections.add(new ASTNode.Section(SectionType.EXPR, accumulator.toString(), pos));
					accumulator.setLength(0);
					pos = null;
					break;
					
				//
				case TemplateParserConstants.CLOSE_SCRIPTLET:
					sections.add(new ASTNode.Section(SectionType.SCRIPTLET, accumulator.toString(), pos));
					accumulator.setLength(0);
					pos = null;
					break;
				default:
					throw new AssertionError("Unexpected kind " + token.kind);
			}
		}
		
		if(accumulator.length() > 0) {
			sections.add(new ASTNode.Section(SectionType.STRING, accumulator.toString(), pos));
			accumulator.setLength(0);
		}
		
		return new ASTNode.Template(Collections.unmodifiableList(sections));
	}
}
