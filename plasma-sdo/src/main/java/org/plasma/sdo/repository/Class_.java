/**
 *         PlasmaSDO™ License
 * 
 * This is a community release of PlasmaSDO™, a dual-license 
 * Service Data Object (SDO) 2.1 implementation. 
 * This particular copy of the software is released under the 
 * version 2 of the GNU General Public License. PlasmaSDO™ was developed by 
 * TerraMeta Software, Inc.
 * 
 * Copyright (c) 2013, TerraMeta Software, Inc. All rights reserved.
 * 
 * General License information can be found below.
 * 
 * This distribution may include materials developed by third
 * parties. For license and attribution notices for these
 * materials, please refer to the documentation that accompanies
 * this distribution (see the "Licenses for Third-Party Components"
 * appendix) or view the online documentation at 
 * <http://plasma-sdo.org/licenses/>.
 *  
 */
package org.plasma.sdo.repository;

import java.util.ArrayList;
import java.util.List;

import org.modeldriven.fuml.repository.OpaqueBehavior;

public class Class_ extends Classifier<org.modeldriven.fuml.repository.Class_> {

	public Class_(org.modeldriven.fuml.repository.Class_ class_) {
		super(class_);
	}
	
	public String findOpaqueBehaviorBody(String name, String language) {
		return getOpaqueBehaviorBody(name, language, true);
	}
	
	public String getOpaqueBehaviorBody(String name, String language) {
		return getOpaqueBehaviorBody(name, language, false);
	}
	
	private String getOpaqueBehaviorBody(String name, String language, 
			boolean supressError) {
		
		String result = null;
		for (OpaqueBehavior behavior : ((org.modeldriven.fuml.repository.Class_)this.element).getOpaqueBehaviors()) {
			if (behavior.getName().equals(name) && behavior.getLanguage().equals(language)) {
				result = behavior.getBody();
			}				
		}
		if (result == null && !supressError)
			throw new RepositoryException("could not find opaque behavior for name: " +
					name + ", language: " + language);
		
		return result;
	}
	
	public List<OpaqueBehavior> getOpaqueBehaviors(String language) {
		
		List<OpaqueBehavior> result = new ArrayList<OpaqueBehavior>();
		for (OpaqueBehavior behavior : ((org.modeldriven.fuml.repository.Class_)this.element).getOpaqueBehaviors()) {
			if (behavior.getLanguage().equalsIgnoreCase(language)) {
				result.add(behavior);
			}				
		}
		return result;
	}
	
}
