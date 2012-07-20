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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;

import org.jboss.weld.bootstrap.WeldBootstrap;
import org.jboss.weld.bootstrap.api.Bootstrap;
import org.jboss.weld.bootstrap.api.Environments;
import org.jboss.weld.bootstrap.api.ServiceRegistry;
import org.jboss.weld.bootstrap.api.helpers.SimpleServiceRegistry;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.jboss.weld.bootstrap.spi.Deployment;
import org.jboss.weld.bootstrap.spi.Metadata;
import org.jboss.weld.exceptions.UnsupportedOperationException;
import org.juzu.impl.spi.cdi.Container;
import org.juzu.impl.spi.fs.ReadFileSystem;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public class WeldContainer extends Container {
	
	private Bootstrap bootstrap;
	
	private BeanManager manager;
	
	private ClassLoader classLoader;

	@Override
	public BeanManager getManager() {
		return manager;
	}

	@Override
	public ClassLoader getClassLoader() {
		return classLoader;
	}

	@Override
	protected void doStart(List<ReadFileSystem<?>> fileSystems) throws Exception {
		this.bootstrap = new WeldBootstrap();
		
		final BeanDeploymentArchiveImpl bda = new BeanDeploymentArchiveImpl(bootstrap, "foo", fileSystems);

		//
		Deployment deployment = new Deployment() {

			final SimpleServiceRegistry registry = new SimpleServiceRegistry();
			
			final List<BeanDeploymentArchive> bdas = Arrays.<BeanDeploymentArchive>asList(bda);
			
			public Collection<BeanDeploymentArchive> getBeanDeploymentArchives() {
				return bdas;
			}

			public Iterable<Metadata<Extension>> getExtensions() {
				return Collections.emptyList();
			}

			public ServiceRegistry getServices() {
				return registry;
			}

			public BeanDeploymentArchive loadBeanDeploymentArchive(Class<?> arg0) {
				throw new UnsupportedOperationException();
			}
		};
		
		bootstrap.startContainer(Environments.SERVLET, deployment);
		bootstrap.startInitialization();
		bootstrap.deployBeans();
		bootstrap.validateBeans();
		bootstrap.endInitialization();
		
		//
		manager = bootstrap.getManager(bda);
		classLoader = bda.getClassLoader();
	}

	@Override
	protected void doStop() {
		if(bootstrap != null) bootstrap.shutdown();
	}
}
