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

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import org.juzu.impl.spi.fs.FileSystem;
import org.juzu.impl.utils.Content;
import org.juzu.impl.utils.Content.ByteArray;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 * Mar 16, 2012
 */
public class CompilerContext<P> {

	final FileSystem<P> fs;
	
	private JavaCompiler compiler;
	
	private VirtualFileManager<P> fileManager;
	
	private Set<Processor> processors;
	
	public CompilerContext(FileSystem<P> fs) {
		this.fs = fs;
		this.compiler = ToolProvider.getSystemJavaCompiler();
		this.fileManager = new VirtualFileManager<P>(fs, compiler.getStandardFileManager(null, null, null));
		this.processors = new HashSet<Processor>();
	}
	
	public void addAnnotationProcessor(Processor annotationProcessorType) {
		if(annotationProcessorType == null) throw new NullPointerException("No null processor allowed");
		processors.add(annotationProcessorType);
	}
	
	public Map<String, Content<?>> getClassOutput() {
		Map<String, Content<?>> map = new HashMap<String, Content<?>>();
		for(Map.Entry<FileKey, VirtualJavaFileObject.RandomAccess> entry : fileManager.classOutput.entrySet() ) {
			map.put(entry.getKey().uri.getPath(), entry.getValue().content);
		}
		return map;
	}
	
	public Set<FileKey> getClassOutputKeys() {
		return fileManager.classOutput.keySet();
	}
	
	public Content<?> getClassOuput(FileKey key) {
		VirtualJavaFileObject.RandomAccess file = fileManager.classOutput.get(key);
		return file != null ? file.content : null;
	}
	
	public Set<FileKey> getSourceOuputKeys() {
		return fileManager.sourceOutput.keySet();
	}
	
	public Content<?> getSourceOutput(FileKey key) {
		VirtualJavaFileObject.RandomAccess file = fileManager.sourceOutput.get(key);
		return file != null ? file.content : null;
	}
	
	public boolean compile() throws IOException {
		Collection<VirtualJavaFileObject.FileSystem<P, P, P>> sources = fileManager.collectJavaFiles();
		fileManager.sourceOutput.clear();
		fileManager.classOutput.clear();
		
		//Filter compiled files
		for(Iterator<VirtualJavaFileObject.FileSystem<P, P, P>> i = sources.iterator(); i.hasNext();) {
			VirtualJavaFileObject.FileSystem<P, P, P> source = i.next();
			FileKey key = source.key;
			VirtualJavaFileObject.RandomAccess.Binary existing = (VirtualJavaFileObject.RandomAccess.Binary)fileManager.classOutput.get(key.as(JavaFileObject.Kind.CLASS));
			//For now we don't support the feature
			/*
			 if(existing != null) {
				ClassFile cf = existing.getFile();
				if(cf != null && cf.getLastModified() >= source.getLastModified()) {
					i.remove();
				}
			}
			*/
		}
		
		JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, null, null, sources);
		task.setProcessors(processors);
		return task.call(); 
	}
}
