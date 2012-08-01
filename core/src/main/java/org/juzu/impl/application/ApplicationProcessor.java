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
package org.juzu.impl.application;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

import org.juzu.Action;
import org.juzu.Application;
import org.juzu.Render;
import org.juzu.URLBuilder;
import org.juzu.application.ApplicationContext;
import org.juzu.application.ApplicationDescriptor;
import org.juzu.application.ControllerMethod;
import org.juzu.application.Phase;
import org.juzu.application.RenderLiteral;
import org.juzu.impl.utils.PackageMap;
import org.juzu.impl.utils.Safe;
import org.juzu.request.RenderContext;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
@javax.annotation.processing.SupportedAnnotationTypes({"org.juzu.Application"})
@javax.annotation.processing.SupportedSourceVersion(SourceVersion.RELEASE_6)
public class ApplicationProcessor extends AbstractProcessor {
	
	static class ApplicationMetaData {
		private final PackageElement packageElt;
		
		private final String fqn;
		
		private final String prefix;
		
		private final String name;
		
		private final String packageName;
		
		private final List<ControllerMetaData> controllers;
		
		ApplicationMetaData(PackageElement packageElt, String applicationName) {
			String packageName = packageElt.getQualifiedName().toString();
			
			this.packageElt = packageElt;
			this.fqn = packageName + "." + applicationName;
			this.name = applicationName;
			this.packageName = packageName;
			this.prefix = packageName + ".";
			this.controllers = new ArrayList<ControllerMetaData>();
		}
	}
	
	static class ControllerMetaData {

		private final List<MethodMetaData> methods;
		
		private final TypeElement typeElt;
		
		ControllerMetaData(TypeElement typeElt) {
			this.typeElt = typeElt;
			this.methods = new ArrayList<MethodMetaData>();
		}
	}
	
	static class MethodMetaData {
		
		private final Phase phase;
		
		private final ExecutableElement element;
		
		private final ExecutableType type;
		
		public MethodMetaData(Phase phase, ExecutableElement element) {
			this.phase = phase;
			this.element = element;
			this.type = (ExecutableType)element.asType();
		}
		
		public CharSequence getName() {
			return element.getSimpleName();
		}
	}
	
	private StringBuilder manifest = new StringBuilder();

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		Filer filer = processingEnv.getFiler();
		
		//Discover all applications
		PackageMap<ApplicationMetaData> applications = new PackageMap<ApplicationMetaData>();
		for(Element elt : roundEnv.getElementsAnnotatedWith(Application.class)) {
			PackageElement packageElt = (PackageElement) elt;
			String packageName = packageElt.getQualifiedName().toString();
			
			//Check that we have no matching application for this package
			if(applications.resolveValue(packageName) != null) {
				throw new UnsupportedOperationException("handle me gracefully");
			}
			
			//
			Application applicationAnn = elt.getAnnotation(Application.class);
			String name = applicationAnn.name();
			if(name.isEmpty()) {
				name = packageElt.getSimpleName().toString();
				if(name.isEmpty()) {
					throw new UnsupportedOperationException("handle me gracefully");
				} else {
					name = Character.toUpperCase(name.charAt(0)) + name.substring(1) + "Application";
				}
				applications.putValue(packageName, new ApplicationMetaData(packageElt, name));
			}
		}
		
		//Collect @Render
		Map<String, ControllerMetaData> controllerMap = new HashMap<String, ControllerMetaData>() ;
		Set<? extends Element> actions = roundEnv.getElementsAnnotatedWith(Action.class);
		Set<? extends Element> renders = roundEnv.getElementsAnnotatedWith(Render.class);
		Set<? extends Element> intersection = new HashSet<Element>(actions);
		intersection.retainAll(renders);
		if(intersection.size() > 0) {
			throw new UnsupportedOperationException("handle me gracefully " + renders);
		}
		
		for(Set<? extends Element> elts : Arrays.asList(actions, renders)) {
			for(Element elt : elts) {
				ExecutableElement executableElt = (ExecutableElement) elt;

				//Find the matching type and the enclosing application
				TypeElement typeElt = (TypeElement)executableElt.getEnclosingElement();

				//
				String typeName = typeElt.getQualifiedName().toString();
				ControllerMetaData a = controllerMap.get(typeName);
				if(a == null) {
					controllerMap.put(typeName, a = new ControllerMetaData(typeElt));

					//Find the matching application
					PackageElement pkg = processingEnv.getElementUtils().getPackageOf(typeElt);
					String fqn = pkg.getQualifiedName().toString();

					//
					ApplicationMetaData found = applications.resolveValue(fqn);
					if(found == null) throw new UnsupportedOperationException("handle me gracefully: could not find application for package " + fqn);
					else found.controllers.add(a);
				}
				
				//
				Phase phase = elts == actions ? Phase.ACTION : Phase.RENDER;
				a.methods.add(new MethodMetaData(phase, executableElt));
			}
		}
		
		//
		for(int i = 0; i < applications.getSize(); i++) 	{
			ApplicationMetaData foo = applications.getValue(i);
			try {
				JavaFileObject jfo = filer.createSourceFile(foo.fqn, foo.packageElt);
				Writer writer = jfo.openWriter();
				try {
					String templatesPackageName = foo.packageName;
					if(templatesPackageName.length() == 0) {
						templatesPackageName = "templates";
					} else {
						templatesPackageName += ".templates";
					}
					
					writer.append("package ").append(foo.packageElt.getQualifiedName()).append(";\n");
					writer.append("import ").append(ApplicationDescriptor.class.getName()).append(";\n");
					writer.append("import ").append(ControllerMethod.class.getName()).append(";\n");
					writer.append("import ").append(Arrays.class.getName()).append(";\n");
					writer.append("public class ").append(foo.name).append(" {\n");
					writer.append("public static final ").append(ApplicationDescriptor.class.getSimpleName());
					writer.append(" DESCRIPTOR = new ").append(ApplicationDescriptor.class.getSimpleName()).append("(");
					writer.append("\"").append(foo.packageName).append("\",");
					writer.append("\"").append(foo.name).append("\",");
					writer.append("\"").append(templatesPackageName).append("\",");
					writer.append("Arrays.<").append(ControllerMethod.class.getSimpleName()).append(">asList(");
					for(ControllerMetaData bar : foo.controllers) {
						for(Iterator<MethodMetaData> j = bar.methods.iterator(); j.hasNext();) {
							MethodMetaData exe = j.next();
							writer.append(bar.typeElt.getQualifiedName()).append("_").append(".").append(exe.getName()).append(".getDescriptor()");
							if(j.hasNext()) 
								writer.append(", ");
						}
					}
					writer.append("));\n");
					writer.append("}\n");
				} finally {
					Safe.close(writer);
				}
			} catch(IOException e) {
				throw new UnsupportedOperationException("handle me gracefully", e);
			}
			
			//
			manifest.append(foo.name).append('=').append(foo.fqn).append("\n");
		}
		
		//
		if(roundEnv.processingOver()) {
			try {
				FileObject fo = filer.createResource(StandardLocation.CLASS_OUTPUT, "org.juzu", "config.properties");
				Writer writer = fo.openWriter();
				try {
					writer.append(manifest);
				} finally {
					Safe.close(writer);
				}
			} catch(IOException e) {
				throw new UnsupportedOperationException("handle me gracefully", e);
			}
		}
		
		//Generate the action literals
		for(Map.Entry<String, ControllerMetaData> entry : controllerMap.entrySet()) {
			try {
				String type = entry.getKey();
				JavaFileObject jfo = filer.createSourceFile(type + "_");
				Writer writer = jfo.openWriter();
				try {
					PackageElement pkg = processingEnv.getElementUtils().getPackageOf(entry.getValue().typeElt);
					writer.append("package ").append(pkg.getQualifiedName()).append(";\n");
					writer.append("import ").append(RenderLiteral.class.getName()).append(";\n");
					writer.append("import ").append(ControllerMethod.class.getName()).append(";\n");
					writer.append("import ").append(Safe.class.getName()).append(";\n");
					writer.append("import ").append(Phase.class.getName()).append(";\n");
					writer.append("import ").append(URLBuilder.class.getName()).append(";\n");
					writer.append("import ").append(ApplicationContext.class.getName()).append(";\n");
					writer.append("import ").append(RenderContext.class.getName()).append(";\n");
					writer.append("public class ").append(entry.getValue().typeElt.getSimpleName()).append("_ {\n");
					
					//
					int index = 0;
					for(MethodMetaData method : entry.getValue().methods) {
						//Method
						writer.append("private static final ").append(ControllerMethod.class.getSimpleName()).append(" method_")
						.append(String.valueOf(index)).append(" = ");
						writer.append("new ").append(ControllerMethod.class.getSimpleName()).append("(");
						writer.append(Phase.class.getSimpleName()).append(".").append(Phase.RENDER.name());
						writer.append(",");
						writer.append(type).append(".class");
						writer.append(',');
						writer.append("Safe.getMethod(").append(type).append(".class,\"").append(method.getName()).append("\"");
						for(TypeMirror foo : method.type.getParameterTypes()) {
							TypeMirror earsed = processingEnv.getTypeUtils().erasure(foo);
							 writer.append(",").append(earsed.toString()).append(".class");
						}
						writer.append(")");
						
						for(VariableElement ve : method.element.getParameters()) {
							writer.append(",\"").append(ve.getSimpleName()).append("\"");
						}
						writer.append(");\n");
						
						//URL builder
						writer.append("public static ").append(URLBuilder.class.getSimpleName()).append(" ").append(method.getName()).append("URL(");
						List<? extends VariableElement> argDecls = method.element.getParameters();
						List<? extends TypeMirror> argTypes = method.type.getParameterTypes();
						for(int i = 0; i < argDecls.size(); i++) {
							if(i > 0) writer.append(',');
							TypeMirror argumentType = argTypes.get(i);
							VariableElement argumentElement = argDecls.get(i);
							writer.append(argumentType.toString()).append(" ").append(argumentElement.getSimpleName().toString());
						}
						
						writer.append(") { return ((RenderContext)ApplicationContext.getCurrentRequest()).createURLBuilder(method_");
						writer.append(Integer.toOctalString(index));
						writer.append("); }\n");
						
						//Maybe remove that
						writer.append("public static final RenderLiteral ").append(method.getName()).append(" = ");
						writer.append("new RenderLiteral(method_").append(Integer.toBinaryString(index)).append(");\n");
						
						index++;
					}
					
					//
					writer.append("}\n");
				} finally {
					Safe.close(writer);
				}
			} catch(IOException e) {
				throw new UnsupportedOperationException("handle me gracefully", e);
			}
		}
		
		//
		return true;
	}
}
