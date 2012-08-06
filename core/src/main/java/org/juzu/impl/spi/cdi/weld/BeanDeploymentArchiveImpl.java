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
package org.juzu.impl.spi.cdi.weld;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jboss.weld.bootstrap.api.ServiceRegistry;
import org.jboss.weld.bootstrap.api.helpers.SimpleServiceRegistry;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.jboss.weld.bootstrap.spi.BeansXml;
import org.jboss.weld.ejb.spi.EjbDescriptor;
import org.jboss.weld.resources.ClassLoaderResourceLoader;
import org.jboss.weld.resources.spi.ResourceLoader;
import org.juzu.impl.spi.fs.ReadFileSystem;
import org.juzu.impl.spi.fs.Visitor;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public class BeanDeploymentArchiveImpl implements BeanDeploymentArchive {
	
	private final WeldContainer owner;
	
	private final String id;
	
	private final Collection<String> beanClasses;
	
	private final BeansXml xml;
	
	private final ServiceRegistry registry;
	
	BeanDeploymentArchiveImpl(
		WeldContainer owner, 
		String id, 
		List<ReadFileSystem<?>> fileSystems) throws IOException {
		
		List<URL> xmlURLs = new ArrayList<URL>();
		List<URL> fsURLs = new ArrayList<URL>();
		final List<String> beanClasses = new ArrayList<String>();
		for(final ReadFileSystem fileSystem : fileSystems) {
			fileSystem.traverse(new Visitor.Default() {
				@Override
				public void file(Object object, String name) throws IOException {
					if(name.endsWith(".class")) {
						StringBuilder fqn = new StringBuilder();
						fileSystem.packageOf(object, '.', fqn);
						if(fqn.length() > 0) fqn.append('.');
						fqn.append(name, 0, name.length() - ".class".length());
						beanClasses.add(fqn.toString());
					}
				}
			});
			
			//
			//fsURLs.add(fileSystem.getURL());
			
			//
			Object beansPath = fileSystem.getPath(Arrays.asList("META-INF", "beans.xml"));
			if(beansPath != null) {
				xmlURLs.add(fileSystem.getURL(beansPath));
			}
		}
		
		//
		BeansXml xml = owner.bootstrap.parse(xmlURLs);
		
		//
		//URLClassLoader classLoader = new URLClassLoader(fsURLs.toArray(new URL[fsURLs.size()]), Thread.currentThread().getContextClassLoader());
		ResourceLoader loader = new ClassLoaderResourceLoader(owner.classLoader);
		
		//
		ServiceRegistry registry = new SimpleServiceRegistry();
		registry.add(ResourceLoader.class, loader);	
		
		//
		this.beanClasses = beanClasses;
		this.xml =xml;
		this.id = id;
		this.registry = registry;
		this.owner = owner;
	}
	

	/**
	 * @see org.jboss.weld.bootstrap.spi.BeanDeploymentArchive#getBeanClasses()
	 */
	public Collection<String> getBeanClasses() {
		return beanClasses;
	}

	/**
	 * @see org.jboss.weld.bootstrap.spi.BeanDeploymentArchive#getBeanDeploymentArchives()
	 */
	public Collection<BeanDeploymentArchive> getBeanDeploymentArchives() {
		return Collections.emptyList();
	}

	/**
	 * @see org.jboss.weld.bootstrap.spi.BeanDeploymentArchive#getBeansXml()
	 */
	public BeansXml getBeansXml() {
		return xml;
	}

	/**
	 * @see org.jboss.weld.bootstrap.spi.BeanDeploymentArchive#getEjbs()
	 */
	public Collection<EjbDescriptor<?>> getEjbs() {
		return Collections.emptyList();
	}

	/**
	 * @see org.jboss.weld.bootstrap.spi.BeanDeploymentArchive#getId()
	 */
	public String getId() {
		return id;
	}

	/**
	 * @see org.jboss.weld.bootstrap.spi.BeanDeploymentArchive#getServices()
	 */
	public ServiceRegistry getServices() {
		return registry;
	}

	public ClassLoader getClassLoader() {
		return owner.classLoader;
	}
}
