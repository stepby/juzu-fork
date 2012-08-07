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
import java.io.Reader;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public class OffsetCharStream extends SimpleCharStream {

	public int beginOffset;
	
	public int currentOffset;
	
	public OffsetCharStream(Reader r) {
		super(r);
	}
	
	public char BeginToken() throws IOException {
		char c = super.BeginToken();
		beginOffset= currentOffset;
		return c;
	}
	
	public char readChar() throws IOException {
		char c = super.readChar();
		currentOffset++;
		return c;
	}
	
	public void backup(int amount) {
		super.backup(amount);
		currentOffset -= amount;
	}
}
