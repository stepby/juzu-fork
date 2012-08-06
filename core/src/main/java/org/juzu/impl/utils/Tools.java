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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public class Tools {
	
	private static Pattern EMPTY_NO_RECURSE = Pattern.compile("");
	
	private static Pattern EMPTY_RECURSE = Pattern.compile(".*");
	
	public static Pattern getPackageMatcher(String packageName, boolean recurse) {
		if(packageName.length() == 0) 
			return recurse ? EMPTY_RECURSE : EMPTY_NO_RECURSE;
		else {
			String regex;
			if(recurse) {
				regex = Pattern.quote(packageName) + "(\\..*)?" ;
			} else {
				regex = Pattern.quote(packageName) ;
			}
			return Pattern.compile(regex);
		}
	}
	
	public static void escape(CharSequence s, StringBuilder appendable) {
		for(int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if(c == '\n')
				appendable.append("\\n");
			else if(c == '\'')
				appendable.append("\\\'");
			else
				appendable.append(c);
		}
	}

	public static boolean safeEquals(Object o1, Object o2) {
		return o1 == null ? o2 == null : o1.equals(o2);
	}

	public static void safeClose(Closeable closeable) {
		if(closeable != null) {
			try {
				closeable.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static Method safeGetMethod(Class<?> type, String name, Class<?> ... parameterTypes) {
		try {
			return type.getDeclaredMethod(name, parameterTypes);
		} catch(NoSuchMethodException e) {
			return null;
		}
	}
	
	public static <T> List<T> safeUnmodifiableList(T ... list) {
		return safeUnmodifiableList(Arrays.asList(list));
	}
	
	public static <T> List<T> safeUnmodifiableList(List<T> list) {
		if(list == null || list.isEmpty()) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(new ArrayList<T>(list));
	}
	
	public static String read(InputStream in) throws IOException {
		return read(in, "UTF-8");
	}

	public static String read(InputStream in, String charsetName) throws IOException {
		byte[] buffer = new byte[256];
		BufferedInputStream bis = new BufferedInputStream(in);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		for(int l = bis.read(buffer); l != -1; l = bis.read(buffer)) {
			baos.write(buffer, 0, l);
		}
		return baos.toString(charsetName);
	}
}
