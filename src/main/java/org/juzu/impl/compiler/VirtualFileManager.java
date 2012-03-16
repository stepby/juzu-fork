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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;

import org.juzu.impl.compiler.VirtualJavaFileObject.CompiledClass;
import org.juzu.impl.compiler.VirtualJavaFileObject.GeneratedSource;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 * Mar 16, 2012
 */
class VirtualFileManager extends ForwardingJavaFileManager<StandardJavaFileManager>{

	final Map<FileKey, VirtualJavaFileObject.Class> files;
	
	final LinkedList<VirtualJavaFileObject.CompiledClass> modifications;
	
	public VirtualFileManager(StandardJavaFileManager fileManager) {
		super(fileManager);
		this.files = new HashMap<FileKey, VirtualJavaFileObject.Class>();
		this.modifications = new LinkedList<VirtualJavaFileObject.CompiledClass>();
	}

	@Override
	public Iterable<JavaFileObject> list(Location location, String packageName, Set<JavaFileObject.Kind> kinds, boolean recurse) throws IOException {
		Iterable<JavaFileObject> s = super.list(location, packageName, kinds, recurse);
		
		List<JavaFileObject> ret = Collections.emptyList();
		if(location == StandardLocation.CLASS_PATH && kinds.contains(JavaFileObject.Kind.CLASS)) {
			Pattern pattern = Tools.getPackageMatcher(packageName, recurse);
			Matcher matcher = null;
			for(VirtualJavaFileObject.Class file : files.values()) {
				if(kinds.contains(file.key.kind)) {
					if(matcher == null) {
						matcher = pattern.matcher(file.className);
					} else {
						matcher.reset(file.className);
					}
					
					if(matcher.matches()) {
						if(ret.isEmpty()) {
							ret = new ArrayList<JavaFileObject>();
						}
						ret.add(file);
					}
				}
			}
		}
		
		if(ret.isEmpty()) {
			return s;
		} else {
			for(JavaFileObject o : s) {
				ret.add(o);
			}
			return ret;
		}
	}
	
	@Override
	public String inferBinaryName(Location location, JavaFileObject file) {
		if(file instanceof VirtualJavaFileObject.Class) {
			VirtualJavaFileObject.Class fileClass = (VirtualJavaFileObject.Class)file;
			return fileClass.className;
		} else {
			return super.inferBinaryName(location, file);
		}
	}
	
	@Override
	public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
		if(kind == JavaFileObject.Kind.CLASS) {
			FileKey key = FileKey.newClassKey(className, JavaFileObject.Kind.CLASS);
			VirtualJavaFileObject.CompiledClass file = (VirtualJavaFileObject.CompiledClass) files.get(key);
			if(file == null) {
				files.put(key, file = new VirtualJavaFileObject.CompiledClass(this, className, key));
			}
			return file;
		} else if(kind == JavaFileObject.Kind.SOURCE) {
			FileKey key = FileKey.newClassKey(className, JavaFileObject.Kind.SOURCE);
			VirtualJavaFileObject.GeneratedSource file = (VirtualJavaFileObject.GeneratedSource)files.get(key);
			if(file == null) {
				files.put(key, file = new VirtualJavaFileObject.GeneratedSource(className, key));
			}
			return file;
		} else {
			throw new UnsupportedOperationException("Kind " + kind + " not supported");
		}
	}
}
