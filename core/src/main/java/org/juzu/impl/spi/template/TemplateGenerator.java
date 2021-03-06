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
package org.juzu.impl.spi.template;

import java.io.IOException;
import java.util.Map;

import javax.annotation.processing.Filer;

import org.juzu.impl.template.SectionType;
import org.juzu.utils.Location;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 * Mar 28, 2012
 */
public abstract class TemplateGenerator {

	public abstract void startScriptlet(Location beginPosition);
	
	public abstract void appendScriptlet(String scriptlet);
	
	public abstract void endScriptlet();
	
	public abstract void startExpression(Location beginPosition);
	
	public abstract void appendExpression(String expr);
	
	public abstract void endExpression();
	
	public abstract void appendText(String text);
	
	public abstract void appendLineBreak(SectionType currentType, Location position);
	
	public abstract void url(String typeName, String name, Map<String, String> args);
	
	public abstract void generate(Filer filer, String pkgName, String rawName) throws IOException;
}
