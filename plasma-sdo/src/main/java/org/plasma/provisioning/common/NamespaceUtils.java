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
package org.plasma.provisioning.common;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NamespaceUtils {

	/**
	 * Tokenizes the given URI as an array of names suitable
	 * to be used as a UML package hierarchy. 
	 * @param uri
	 * @return the name array
	 */
	public static String[] toPackageTokens(String uri)
	{
		List<String> list = new ArrayList<String>();
		
		try {
			URL url = new URL(uri);
			String[] authority = url.getAuthority().split("\\.");
			for (int i = authority.length -1; i >= 0; i--)
				if (authority[i] != null && authority[i].trim().length() > 0)
				    list.add(authority[i]);
			if (url.getPath() != null) {
			    String[] path = url.getPath().split("\\/");
			    for (int i = 0; i < path.length; i++)
					if (path[i] != null && path[i].trim().length() > 0)
				        list.add(path[i]);
			}
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(e); 
		}
		
		String[] result = new String[list.size()];
		list.toArray(result);		
		
		return result;		
	}

	/**
	 * Tokenizes the given URI and assembles a qualified java package name. 
	 * @param uri
	 * @return the qualified package name  
	 */
	public static String toPackageName(String uri) {
		String[] tokens = toPackageTokens(uri);
		StringBuilder buf = new StringBuilder();
		for (int i = tokens.length-1; i >= 0; i--) {
			if (i < tokens.length-1)
				buf.append(".");
			buf.append(tokens[i]);
		}
		return buf.toString();
	}

}
