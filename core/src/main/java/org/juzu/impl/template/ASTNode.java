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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.juzu.impl.spi.template.TemplateGenerator;
import org.juzu.utils.Location;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 * Mar 28, 2012
 */
public abstract class ASTNode {

	private final Location pos;
	
	public ASTNode(Location pos) {
		if(pos ==  null) throw new NullPointerException("No null position accepted");
		this.pos = pos;
	}
	
	public Location getPosition() {
		return pos;
	}
	
	public static class Template extends ASTNode {

		private final List<Section> sections;
		
		Template(List<Section> sections) {
			super(new Location(0, 0));
			this.sections = sections;
		}
		
		public List<Section> getSections() {
			return sections;
		}
		
		public void generate(TemplateGenerator generator) {
			GeneratorContext ctx = new GeneratorContext(generator);
			for(ASTNode.Section section : sections) {
				ctx.begin(section.getType());
				for(ASTNode item : section.getItems()) {
					ctx.append(item);
				}
				ctx.end();
			}
		}
		
		private class GeneratorContext {
			
			private SectionType currentType = null;
			
			private StringBuilder accumulateText = new StringBuilder();
			
			private TemplateGenerator writer;
			
			GeneratorContext(TemplateGenerator writer) {
				this.writer = writer;
			}
			
			void begin(SectionType sectionType) {
				if(sectionType == null) throw new NullPointerException();
				if(currentType != null) throw new IllegalStateException();
				this.currentType = sectionType;
				switch (currentType) {
					case STRING :
						break;
					case SCRIPTLET:
						writer.startScriptlet();
						break;
					case EXPR:
						writer.startExpression();
						break;
				}
			}
			
			void append(ASTNode item) {
				if(item instanceof ASTNode.Text) {
					ASTNode.Text textItem = (ASTNode.Text) item;
					String text = textItem.getData();
					switch (currentType) {
						case STRING :
							accumulateText.append(text);
							break;
						case SCRIPTLET :
							writer.appendScriptlet(textItem);
							break;
						case EXPR :
							writer.appendExpression(textItem);
							break;
					}
				} else if(item instanceof ASTNode.LineBreak) {
					switch (currentType) {
						case STRING :
							accumulateText.append('\n');
							break;
						case SCRIPTLET :
							writer.appendLineBreak(currentType);
							break;
						case EXPR :
							writer.appendLineBreak(currentType);
							break;
					}
				} else  throw new AssertionError();
			}
			
			void end() {
				if(currentType == null) throw new IllegalStateException();
				switch (currentType) {
					case STRING :
						if(accumulateText.length() > 0) {
							writer.appendText(accumulateText.toString());
							accumulateText.setLength(0);
						}
						break;
					case SCRIPTLET:
						writer.endScriptlet();
						break;
					case EXPR:
						writer.endExpression();
						break;
				}
				this.currentType = null;
			}
		}
	}
	
	public static class Text extends ASTNode {
		
		private final String data;

		Text(Location pos, String data) {
			super(pos);
			if(data == null) throw new NullPointerException();
			this.data = data;
		}
		
		public String getData() {
			return data;
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + "[position=" + getPosition() + ", data=" + data + "]"; 
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj == this) return true;
			if(obj instanceof Text) {
				Text that = (Text) obj;
				return data.equals(that.data);
			}
			return false;
		}
	}
	
	public static class Section {
		
		private final SectionType type;
		private final List<ASTNode> items;
		
		Section(SectionType type, String text) {
			this(type, text, 0, 0);
		}
		
		Section(SectionType type, String text, Location pos) {
			this(type, text, pos.getCol(), pos.getLine());
		}
		
		Section(SectionType type, String text, int colNumber, int lineNumber) {
			if(type == null) throw new NullPointerException();
			if(text == null) throw new NullPointerException();
			
			//
			List<ASTNode> sections = new ArrayList<ASTNode>();
			
			int from = 0;
			while(true) {
				int to = text.indexOf('\n', from);
				if(to != -1) {
					String chunk = text.substring(from, to);
					sections.add(new Text(new Location(colNumber, lineNumber), chunk));
					sections.add(new LineBreak(new Location(colNumber + (to - from), lineNumber)));
					from = to + 1;
					lineNumber++;
					colNumber = 1;
				} else {
					String chunk = text.substring(from);
					sections.add(new Text(new Location(colNumber, lineNumber), chunk));
					break;
				}
			}
			
			this.type = type;
			this.items = Collections.unmodifiableList(sections);
		}
		
		public SectionType getType() {
			return type;
		}
		
		public List<ASTNode> getItems() {
			return items;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj == this) return true;
			if(obj instanceof Section) {
				Section that = (Section) obj;
				return type == that.type && items.equals(that.items);
			}
			return false;
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + "[type=" + type + ", items=" + items + "]";
		}
	}
	
	public static class LineBreak extends  ASTNode {

		LineBreak(Location pos) {
			super(pos);
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + "[position=" + getPosition() + "]";
		}
		
		@Override
		public boolean equals(Object obj) {
			return obj == this || obj instanceof LineBreak;
		}
	}
}
