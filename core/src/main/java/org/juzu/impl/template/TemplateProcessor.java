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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.VariableElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import org.juzu.Resource;
import org.juzu.impl.application.ApplicationProcessor;
import org.juzu.impl.application.ApplicationProcessor.ApplicationMetaData;
import org.juzu.impl.compiler.ProcessorPlugin;
import org.juzu.impl.spi.template.MethodInvocation;
import org.juzu.impl.spi.template.TemplateGenerator;
import org.juzu.impl.spi.template.TemplateGeneratorContext;
import org.juzu.impl.spi.template.TemplateProvider;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 * May 21, 2012
 */

@SupportedAnnotationTypes({ "org.juzu.Resource" })
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class TemplateProcessor extends ProcessorPlugin {
	
	private static final Pattern PROVIDER_PKG_PATTERN = Pattern.compile(
		"org\\.juzu\\.impl\\.spi\\.template\\.([^.]+)(?:\\..+)?"
	);
	
	private static final Pattern NAME_PATTERN = Pattern.compile("([^.]+)\\.([a-zA-Z]+)");
	
	private Map<String, TemplateProvider> providers;
	
	private ApplicationProcessor applicationPlugin;
	
	@Override
	public void init() {
		
		//Discover the template provider
		ServiceLoader<TemplateProvider> loader = ServiceLoader.load(TemplateProvider.class, TemplateProvider.class.getClassLoader());
		Map<String, TemplateProvider> providers = new HashMap<String, TemplateProvider>();
		for(TemplateProvider provider : loader) {
			String pkgName = provider.getClass().getPackage().getName();
			Matcher matcher = PROVIDER_PKG_PATTERN.matcher(pkgName);
			if(matcher.matches()) {
				String extension = matcher.group(1);
				providers.put(extension, provider);
			}
		}
		this.providers = providers;
		this.applicationPlugin = getPlugin(ApplicationProcessor.class);
	}

	@Override
	public void process() {
		ASTBuilder parser = new ASTBuilder();

		//
		for(Element elt : getElementsAnnotatedWith(Resource.class)) {
			PackageElement packageElt = getPackageOf(elt);
			Resource ref = elt.getAnnotation(Resource.class);
			String value = ref.value();
			Matcher matcher = NAME_PATTERN.matcher(value);
			if(!matcher.matches()) {
				throw new UnsupportedOperationException("handle me gracefully for name " + value);
			}
			
			//Find the closets enclosing application
			final ApplicationMetaData application = applicationPlugin.getApplication(packageElt);
			if(application == null) throw new UnsupportedOperationException("handle me gracefully");
			StringBuilder templatePkgSB = new StringBuilder(application.getPackageName());
			if(templatePkgSB.length() > 0) templatePkgSB.append('.');
			templatePkgSB.append("templates");
			String templatePkgFQN = templatePkgSB.toString();
			
			//
			try {
				FileObject file = getResource(StandardLocation.SOURCE_PATH, templatePkgFQN, value);
				CharSequence content = file.getCharContent(false).toString();
				
				String extension = matcher.group(2);
				TemplateProvider provider = providers.get(extension);
				if(provider != null) {
					//TODO
					TemplateGenerator generator = provider.newGenerator(new TemplateGeneratorContext() {
						
						public MethodInvocation resolveMethodInvocation(String name, Map<String, String> parameterMap) {
							ApplicationProcessor.MethodMetaData methodMD = application.resolve(name, parameterMap.keySet());
							List<String> args = new ArrayList<String>();
							for(VariableElement ve : methodMD.getElement().getParameters()) {
								String value = parameterMap.get(ve.getSimpleName().toString());
								args.add(value);
							}
							return new MethodInvocation(application.getClassName(), methodMD.getName() + "URL", args);
						}
					});
					
					//
					parser.parse(content).generate(generator);
					generator.generate(getFiler(), templatePkgFQN, matcher.group(1));
				} else {
					throw new UnsupportedOperationException("handle me gracefully");
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
}
