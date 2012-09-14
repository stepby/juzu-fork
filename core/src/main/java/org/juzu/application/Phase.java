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
package org.juzu.application;

import java.lang.annotation.Annotation;

import org.juzu.Action;
import org.juzu.Render;
import org.juzu.Resource;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public enum Phase {
	
	/**
	 * Action phase
	 */
	ACTION(Action.class),
	
	/**
	 * Render phase
	 */
	RENDER(Render.class),
	
	/**
	 * Resource phase
	 */
	RESOURCE(Resource.class);
	
	/** . */
	public final Class<? extends Annotation> annotation;
	
	Phase(Class<? extends Annotation> annotation) {
		this.annotation = annotation;
	}
}
