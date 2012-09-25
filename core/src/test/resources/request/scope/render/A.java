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
package request.scope.render;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.juzu.Action;
import org.juzu.FlashScoped;
import org.juzu.Render;
import org.juzu.RenderScoped;
import org.juzu.Resource;
import org.juzu.test.Registry;
import org.juzu.test.support.Car;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public class A
{
	@Inject
	private Car car;
	
	@Produces
	@RenderScoped
	public static Car create() {
		return new Car();
	}
	
	@Action
	public void action() {
		try {
			long code = car.getIdentityHashCode();
			Registry.set("car", code);
		} catch(ContextNotActiveException e) {}
	}
	
	@Render
	public void index() {
		Registry.set("car", car.getIdentityHashCode());
		Registry.set("action", A_.actionURL().toString());
		Registry.set("resource", A_.resourceURL().toString());
	}
	
	@Resource
	public void resource() {
		try {
			long code = car.getIdentityHashCode();
			Registry.set("car", code);
		} catch(ContextNotActiveException e) {}
	}
}
