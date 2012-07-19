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
package org.juzu.text;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.UndeclaredThrowableException;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 * Mar 28, 2012
 */
public interface CharArray {

	int getLength();
	
	void write(OutputStream out	) throws IOException, NullPointerException;
	
	void write(Appendable appendable) throws IOException, NullPointerException;
	
	public static class Simple implements CharArray {
		
		private static final Charset UTF_8 = Charset.forName("UTF-8");

		private final CharSequence chars;
		
		private final byte[] bytes;
		
		private Simple(CharSequence chars) {
			try {
				this.chars = chars;
				this.bytes = UTF_8.newEncoder().encode(CharBuffer.wrap(chars)).array();
			} catch(CharacterCodingException e) {
				throw new UndeclaredThrowableException(e);
			}
		}
		
		public int getLength() {
			return chars.length();
		}

		public void write(OutputStream out) throws IOException, NullPointerException {
			if(out == null) throw new NullPointerException();
			out.write(bytes);
		}

		public void write(Appendable appendable) throws IOException, NullPointerException {
			if(appendable == null) throw new NullPointerException();
			appendable.append(chars);
		}
	}
}
