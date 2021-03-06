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
package org.juzu.impl.utils;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 * Apr 2, 2012
 */
public class CharSequenceReaderTestCase extends TestCase {

	public void testEmpty() {
		CharSequenceReader reader = new CharSequenceReader("");
		assertEquals(-1, reader.read());
	}
	
	public void testSimple() {
		CharSequenceReader reader = new CharSequenceReader("foo");
		assertEquals('f', reader.read());
		assertEquals('o', reader.read());
		assertEquals('o', reader.read());
		assertEquals(-1, reader.read());
	}
	
	public void testUnread() {
		CharSequenceReader reader = new CharSequenceReader("");
		reader.unread('o');
		reader.unread('o');
		reader.unread('f');
		assertEquals('f', reader.read());
		assertEquals('o', reader.read());
		assertEquals('o', reader.read());
		assertEquals(-1, reader.read());
	}
}
