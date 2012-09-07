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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.juzu.AmbiguousResolutionException;
import org.juzu.application.ApplicationDescriptor;
import org.juzu.application.Phase;
import org.juzu.impl.request.ControllerMethod;
import org.juzu.impl.request.ControllerParameter;
import org.juzu.impl.utils.Tools;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
public class ControllerResolver {

	private final List<ControllerMethod> methods;
	
	public ControllerResolver(ApplicationDescriptor desc) throws NullPointerException {
		if(desc == null) throw new NullPointerException("No null application descriptor accepted");
		
		//
		this.methods = desc.getControllerMethods();
	}
	
	public ControllerResolver(ControllerMethod ... methods) {
		this.methods = Tools.safeUnmodifiableList(methods);
	}
	
	private static class Match {
		private final ControllerMethod method;
		
		private final int score;
		
		public Match(ControllerMethod method, int score) {
			this.method = method;
			this.score = score;
		}
	}
	
	public ControllerMethod resolve(Phase phase, Map<String, String[]> parameters) throws AmbiguousResolutionException {
		String methodName;
		String[] op = parameters.get("op");
		if(op != null && op.length > 0) {
			methodName = op[0];
		} else {
			methodName = "index";
		}
		
		//
		TreeMap<Integer, List<Match>> matches = new TreeMap<Integer, List<Match>>();
		out:
		for(ControllerMethod method : methods) {
			if(method.getPhase() == phase && methodName.equals(method.getMethodName())) {
				int score = 0;
				List<List<ControllerParameter>> listList = new  ArrayList<List<ControllerParameter>>();
				listList.add(method.getArgumentParameters());
				for(List<ControllerParameter> list : listList) {
					for(ControllerParameter cp : list) {
						String[] val = parameters.get(cp.getName());
						if(val == null || val.length ==0 || (cp.getValue() != null && !cp.getValue().equals(val[0]))) {
							continue out;
						}
						score += cp.getValue() == null ? 1 : 2;
					}
				}
				List<Match> scoreMatches = matches.get(score);
				if(scoreMatches == null) {
					matches.put(score, scoreMatches = new ArrayList<Match>());
				}
				scoreMatches.add(new Match(method, score));
			}
		}
		
		//Return the best match
		Map.Entry<Integer, List<Match>> a = matches.lastEntry();
		if(a != null) {
			List<Match> b = a.getValue();
			if(b.size() > 1) throw new AmbiguousResolutionException("Could not resolve resolution");
			return b.get(0).method;
		}
		
		//
		return null;
	}
}
