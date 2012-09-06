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

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes({"*"})
public class Processor extends AbstractProcessor {
	
	final List<ProcessorPlugin> plugins;
	
	ProcessingEnvironment processingEnv;
	
	RoundEnvironment roundEnv;
	
	public Processor(List<ProcessorPlugin> plugins) {
		this.plugins = plugins;
	}
	
	public Processor(ProcessorPlugin ... plugins) {
		this.plugins = Arrays.asList(plugins);
	}
	
	@Override
	public void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		
		//
		this.processingEnv = processingEnv;
		
		//
		for(ProcessorPlugin plugin : plugins) {
			plugin.processor = this;
		}
		
		//
		for(ProcessorPlugin plugin : plugins) {
			plugin.init();
		}
	}
	
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		this.roundEnv = roundEnv;
		
		try {
			//Process
			for(ProcessorPlugin plugin : plugins) {
				plugin.process();
			}
			
			//Over
			if(roundEnv.processingOver()) {
				for(ProcessorPlugin plugin : plugins) {
					plugin.over();
				}
			}
		} catch(Exception e) {
			if(e instanceof CompilationException) {
				CompilationException ce = (CompilationException) e;
				processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, ce.getMessage(), ce.getElement());
			} else {
				String msg = e.getMessage();
				if(msg == null) msg = "Exception: " + e.getClass().getName();
				processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg);
			}
		} finally {
			this.roundEnv = null;
		}
		
		//
		return true;
	}
}
