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
import java.util.List;

import org.apache.commons.io.input.CharSequenceReader;
import org.juzu.impl.template.ASTNode.Section;
import org.juzu.utils.Location;


/**
 * Author : Nguyen Thanh Hai
 *          haithanh0809@gmail.com
 * Aug 7, 2012  
 */
public class ASTBuilder {

	public ASTNode.Template parse(CharSequence s) {
		return build(s, new CharSequenceReader(s));
	}
	
	private ASTNode.Template build(CharSequence s, CharSequenceReader reader) {
		//
		TemplateParser parser = new TemplateParser(new OffsetTokenManager(new OffsetCharStream(reader)));
		
		//
		try {
			parser.parse();
		} catch(ParseException e) {
			throw new AssertionError(e);
		}
		
		//
		List<ASTNode.Section> sections = new ArrayList<ASTNode.Section>();
		int previousOffset = 0;
		Location previousPosition = new Location(1, 1);
		for(int i = 0; i < parser.list.size(); i++) {
			ASTNode.Section section = parser.list.get(i);
			//
			if(section.getBeginOffset() > previousOffset) {
				sections.add(new ASTNode.Section(
					SectionType.STRING,
					previousOffset,
					section.getBeginOffset(),
					s.subSequence(previousOffset, section.getBeginOffset()).toString(),
					previousPosition,
					section.getEndPosition()));
			}
			sections.add(section);
			previousOffset = section.getEndOffset();
			previousPosition = section.getEndPosition();
		}
		
		//
		if(previousOffset < s.length()) {
			sections.add(new ASTNode.Section(
				SectionType.STRING,
				previousOffset,
				s.length(),
				s.subSequence(previousOffset, s.length()).toString(),
				previousPosition,
				new Location(parser.token.endColumn, parser.token.endLine)));
		}
		//
		return new ASTNode.Template(Collections.unmodifiableList(sections));
	}
}
