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
package org.juzu.impl.compiler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;

import javax.tools.SimpleJavaFileObject;

import org.juzu.impl.spi.fs.Content;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 * Mar 16, 2012
 */
class VirtualJavaFileObject extends SimpleJavaFileObject {
	
	final FileKey key;

	VirtualJavaFileObject(FileKey key) {
		super(key.uri, key.kind);
		this.key = key;
	}
	
	static class FileSystem<P, D extends P, F extends P> extends VirtualJavaFileObject {
		
		private final F file;
		
		private final org.juzu.impl.spi.fs.FileSystem<P, D, F> fs;
		
		private CharSequence content;
		
		private long lastModified;

		FileSystem(org.juzu.impl.spi.fs.FileSystem<P, D, F> fs, F file,  FileKey key) throws IOException {
			super(key);
			this.fs = fs;
			this.file = file;
		}
		
		@Override
		public long getLastModified() {
			if(lastModified == 0) {
				try {
					lastModified = fs.getLastModified(file);
				} catch(IOException ignore) {
//					return 0;
				}
			}
			return lastModified;
		}
		
		@Override
		public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
			long lastModified = fs.getLastModified(file);
			
			if(content == null || this.lastModified < lastModified) {
				Content content = fs.getContent(file);
				this.content = content.getValue();
				this.lastModified = content.getLastModified();
			}
			
			return content;
		}
	}
	
	static class Class extends VirtualJavaFileObject {

		final String className;
		
		Class(FileKey key, String className) {
			super(key);
			this.className = className;
		}
	}
	
	static class CompiledClass extends Class {

		private ByteArrayOutputStream out;
		
		private ClassFile file;
		
		private VirtualFileManager manager;
		
		CompiledClass(VirtualFileManager manager, String className, FileKey key) {
			super(key, className);
			this.file = null;
			this.out = null;
			this.manager = manager;
		}
		
		ClassFile getFile() {
			return file;
		}
		
		@Override
		public long getLastModified() {
			return file == null ? 0 : file.getLastModified();
		}
		
		@Override
		public OutputStream openOutputStream() throws IOException {
			file = null;
			out = new ByteArrayOutputStream() {
				@Override
				public void close() throws IOException {
					file = new ClassFile(className, toByteArray());
					out = null;
					manager.modifications.add(CompiledClass.this);
				}
			};
			return out;
		}
		
		@Override
		public InputStream openInputStream() throws IOException {
			if(file != null) 
				return new ByteArrayInputStream(file.getBytes());
			else 
				throw new IOException("No content");
		}
	}
	
	static class GeneratedResource extends VirtualJavaFileObject {
		
		private StringWriter writer;
		
		private String content;

		GeneratedResource(FileKey key) {
			super(key);
			this.writer = null;
			this.content = null;
		}
		
		@Override
		public Writer openWriter() throws IOException {
			content = null;
			writer = new StringWriter() {
				@Override
				public void close() throws IOException {
					content = toString();
					writer = null;
				}
			};
			return writer;
		}
		
		@Override
		public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
			return content;	
		}
	}
	
	static class GeneratedSource extends Class {

		private StringWriter writer;
		
		private String content;
		
		GeneratedSource(String className, FileKey key) {
			super(key, className);
			this.content = null;
			this.writer = null;
		}
		
		@Override
		public Writer openWriter() throws IOException {
			content = null;
			writer = new StringWriter() {
				@Override
				public void close() throws IOException {
					content = toString();
					writer = null;
				}
			};
			return writer;
		}
		
		@Override
		public CharSequence getCharContent(boolean ignoreEncodingError) throws IOException {
			return content;
		}
	}
}
