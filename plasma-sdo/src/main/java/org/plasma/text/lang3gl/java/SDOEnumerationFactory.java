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
package org.plasma.text.lang3gl.java;

import org.plasma.metamodel.Class;
import org.plasma.metamodel.Enumeration;
import org.plasma.metamodel.EnumerationLiteral;
import org.plasma.metamodel.Package;
import org.plasma.sdo.PlasmaEnum;
import org.plasma.text.TextBuilder;
import org.plasma.text.lang3gl.EnumerationFactory;
import org.plasma.text.lang3gl.Lang3GLContext;

public class SDOEnumerationFactory extends SDODefaultFactory
    implements EnumerationFactory {

	public SDOEnumerationFactory(Lang3GLContext context) {
		super(context);
	}	

	public String createFileName(Enumeration clss) {
		TextBuilder buf = new TextBuilder(LINE_SEP, this.context.getIndentationToken());
		buf.append(clss.getName());
		buf.append(".java");		
		return buf.toString();
	}

	public String createContent(Package pkg, Enumeration enm) {
		TextBuilder buf = new TextBuilder(LINE_SEP, this.context.getIndentationToken());
		buf.append(this.createPackageDeclaration(pkg));
		buf.append(LINE_SEP);
		buf.append(this.createThirdPartyImportDeclarations(pkg, null));
		buf.append(LINE_SEP);
		buf.append(this.createSDOInterfaceReferenceImportDeclarations(pkg, null));
		buf.append(LINE_SEP);
		buf.append(LINE_SEP);
		buf.append(this.createTypeDeclaration(pkg, enm));
		buf.append(LINE_SEP);
		buf.append(this.beginBody());
		
		buf.append(LINE_SEP);
		buf.append(this.createStaticFieldDeclarations(enm));
		buf.append(LINE_SEP);
		buf.append(this.createPrivateFieldDeclarations(enm));
		buf.append(LINE_SEP);
		buf.append(this.createConstructors(pkg, enm));
		buf.append(LINE_SEP);
		buf.append(this.createOperations(pkg, enm));
		
		for (EnumerationLiteral lit : enm.getEnumerationLiterals()) {
			buf.append(LINE_SEP);
			buf.append(this.createOperations(pkg, enm, lit));
		}		
		
		buf.append(LINE_SEP);
		buf.append(this.endBody());
		return buf.toString();
	}
	
	protected String createTypeDeclaration(Package pkg, Enumeration enumeration) {
		TextBuilder buf = new TextBuilder(LINE_SEP, this.context.getIndentationToken());
		
		String javadocs = createTypeDeclarationJavadoc(pkg, enumeration);
	    buf.append(javadocs);	
		
	    buf.append(newline(0));	
		buf.append("public enum ");
		buf.append(enumeration.getName());
		buf.append(" implements ");
		buf.append(PlasmaEnum.class.getSimpleName());
		return buf.toString();
	}

	private String createTypeDeclarationJavadoc(Package pkg, Enumeration enm) {
		TextBuilder buf = new TextBuilder(LINE_SEP, this.context.getIndentationToken());
		
		buf.append("/**"); // begin javadoc
		
		// add formatted doc from UML if exists		
		// always put model definition first so it appears
		// on package summary line for class
		String docs = getWrappedDocmentations(enm.getDocumentations(), 0);
		if (docs.trim().length() > 0) {
		    buf.append(docs);	
		    // if we have model docs, set up the next section w/a "header"
		    buf.append(newline(0));	
			buf.append(" * <p></p>");
		}
		
	    buf.append(newline(0));	
		buf.append(" * This generated <a href=\"http://docs.plasma-sdo.org/api/org/plasma/sdo/PlasmaEnum.html\">Enumeration</a> represents the domain model enumeration <b>");
		buf.append(enm.getName());
		buf.append("</b> which is part of namespace <b>");
		buf.append(enm.getUri());
		buf.append("</b> as defined within the <a href=\"http://docs.plasma-sdo.org/api/org/plasma/config/package-summary.html\">Configuration</a>.");
		buf.append(newline(0));	
		buf.append(" * <p></p>");	
		buf.append(" * Generated <a href=\"http://plasma-sdo.org\">SDO</a> enumerations embody not only logical-name literals ");
		buf.append(newline(0));	
		buf.append(" * but also physical or instance names, which are often shorter (possibly abbreviated) ");
		buf.append(newline(0));	
		buf.append(" * and applicable as a data-store space-saving device. ");
		buf.append(newline(0));	
		buf.append(" * Application programs should typically use the physical or instance name ");
		buf.append(newline(0));	
		buf.append(" * for an enumeration literal when setting a data object property which is <a href=\"http://docs.plasma-sdo.org/api/org/plasma/sdo/EnumerationConstraint.html\">constrained</a> by an enumeration.");

	    buf.append(newline(0));	
		buf.append(" */"); // end javadoc
		
		return buf.toString();
	}	
	
	protected String createConstructors(Package pkg, Enumeration enm) {
		TextBuilder buf = new TextBuilder(LINE_SEP, this.context.getIndentationToken());
		buf.appendln(1, "private ");
		buf.append(enm.getName());
		buf.append("(String instanceName, String description) {");
		buf.appendln(1, "    this.instanceName = instanceName;");
		buf.appendln(1, "    this.description = description;");
		buf.appendln(1, "}");
		buf.append(LINE_SEP);        
		return buf.toString();
	}

	protected String createOperations(Package pkg, Enumeration enm) {
		TextBuilder buf = new TextBuilder(LINE_SEP, this.context.getIndentationToken());

		buf.appendln(1, "/**"); 
		buf.appendln(1, "* Returns the logical name associated with this enumeration literal.");
		buf.appendln(1, "*/"); 
		buf.appendln(1, "public String getName()");
		buf.appendln(1, "{");
		buf.appendln(1, "    return this.name();");
		buf.appendln(1, "}");
		buf.append(LINE_SEP);        

		buf.appendln(1, "/**"); 
		buf.appendln(1, "* Returns the physical or instance name associated with this enumeration literal.");
		buf.appendln(1, "*/"); 
		buf.appendln(1, "public String getInstanceName() {");
		buf.appendln(1, "    return this.instanceName;");
		buf.appendln(1, "}");
		buf.append(LINE_SEP);        

		buf.appendln(1, "/**"); 
		buf.appendln(1, "* Returns the descriptive text associated with this enumeration literal.");
		buf.appendln(1, "*/"); 
		buf.appendln(1, "public String getDescription() {");
		buf.appendln(1, "    return this.description;");
		buf.appendln(1, "}");
		buf.append(LINE_SEP);        

		buf.appendln(1, "/**"); 
		buf.appendln(1, "* Returns the enum values for this class as an array of implemented interfaces");
		buf.appendln(1, "* @see "); 
		buf.append(PlasmaEnum.class.getSimpleName()); 
		buf.appendln(1, "*/"); 
		buf.appendln(1, "public static ");
		buf.append(PlasmaEnum.class.getSimpleName());
		buf.append("[] enumValues()");
		buf.appendln(1, "{");
		buf.appendln(1, "    return values();");
		buf.appendln(1, "}");
		buf.append(LINE_SEP);   
		
		buf.appendln(1, "/**"); 
		buf.appendln(1, "* Returns the enumeration value matching the given name.");
		buf.appendln(1, "*/"); 
		buf.appendln(1, "public static ");
		buf.append(enm.getName());
		buf.append(" fromName(String name) {");
		buf.append(this.newline(2));
		buf.append("for (");
		buf.append(PlasmaEnum.class.getSimpleName());
		buf.append(" enm : enumValues()) {");
		buf.append(this.newline(3));
		buf.append("if (enm.getName().equals(name))");
		buf.append(this.newline(4));
		buf.append("return (");
		buf.append(enm.getName());
		buf.append(")enm;");
		buf.append(this.newline(2));
		buf.append("}");
		buf.append(this.newline(3));
		buf.append("throw new ");
		buf.append(IllegalArgumentException.class.getSimpleName());
		buf.append("(\"no enumeration value found for name '\" + name + \"'\");");
		buf.appendln(1, "}");		
		buf.append(LINE_SEP);   

		buf.appendln(1, "/**"); 
		buf.appendln(1, "* Returns the enumeration value matching the given physical or instance name.");
		buf.appendln(1, "*/"); 
		buf.appendln(1, "public static ");
		buf.append(enm.getName());
		buf.append(" fromInstanceName(String instanceName) {");
		buf.append(this.newline(2));
		buf.append("for (");
		buf.append(PlasmaEnum.class.getSimpleName());
		buf.append(" enm : enumValues()) {");
		buf.append(this.newline(3));
		buf.append("if (enm.getInstanceName().equals(instanceName))");
		buf.append(this.newline(4));
		buf.append("return (");
		buf.append(enm.getName());
		buf.append(")enm;");
		buf.append(this.newline(2));
		buf.append("}");
		buf.append(this.newline(3));
		buf.append("throw new ");
		buf.append(IllegalArgumentException.class.getSimpleName());
		buf.append("(\"no enumeration value found for instance name '\" + instanceName + \"'\");");
		buf.appendln(1, "}");
		
		return buf.toString();
	}

	protected String createOperations(Package pkg, Enumeration enm,
			EnumerationLiteral lit) {
		// TODO Auto-generated method stub
		return "";
	}

	protected String createSDOInterfaceReferenceImportDeclarations(Package pkg, Class clss) {
		TextBuilder buf = new TextBuilder(LINE_SEP, this.context.getIndentationToken());
		return buf.toString();
	}

	protected String createThirdPartyImportDeclarations(Package pkg, Class clss) {
		TextBuilder buf = new TextBuilder(LINE_SEP, this.context.getIndentationToken());
		buf.append("import ");
		buf.append(PlasmaEnum.class.getName());
		buf.append(";");
		return buf.toString();
	}
	
	protected String createStaticFieldDeclarations(Enumeration enumeration) {
		TextBuilder buf = new TextBuilder(LINE_SEP, this.context.getIndentationToken());
		for (int i = 0; i < enumeration.getEnumerationLiterals().size(); i++) {
			EnumerationLiteral literal = enumeration.getEnumerationLiterals().get(i);
			if (i > 0)
    			buf.append(",");    					
			buf.appendln(1, createEnumerationLiteralDeclarationJavadoc(enumeration, literal));
			buf.appendln(1, toEnumLiteralName(literal.getName()));
			if (literal.getAlias() != null && 
				literal.getAlias().getPhysicalName() != null && 
				literal.getAlias().getPhysicalName().length() > 0) {
			    buf.append("(\"");
			    buf.append(literal.getAlias().getPhysicalName());
			    buf.append("\"");
			}
			else {
			    buf.append("(null");
			}
			buf.append(",\"");
			if (literal.getDocumentations() != null && literal.getDocumentations().size() > 0) {
				String body = literal.getDocumentations().get(0).getBody().getValue();			    
				body = replaceQuot(body);
				body = body.replace('\n', ' ');
				buf.append(body);
			}
			buf.append("\")");
		}
	    buf.append(";");    					
		buf.append(LINE_SEP);
		return buf.toString();
	}
	
	private String createEnumerationLiteralDeclarationJavadoc(Enumeration enumeration, EnumerationLiteral literal)
	{
		TextBuilder buf = new TextBuilder(LINE_SEP, this.context.getIndentationToken());
		buf.appendln(1, "/**"); // begin javadoc
		
		// add formatted doc from UML if exists		
		// always put model definition first so it appears
		// on package summary line for class
		String docs = getWrappedDocmentations(literal.getDocumentations(), 1);
		if (docs.trim().length() > 0) {
		    buf.append(docs);	
		    buf.appendln(1, " * <p></p>");
		}
		
        buf.appendln(1, " * Holds the logical and physical names for literal <b>");
		buf.append(literal.getName());
		buf.append("</b>."); 					
		
		buf.appendln(1, " */"); // end javadoc			
		return buf.toString();
	}	
	
	
	private String replaceQuot(String s) {
	   return s.replaceAll("\"", "\\\\\""); 	
	}

	protected String createPrivateFieldDeclarations(Enumeration enm) {
		TextBuilder buf = new TextBuilder(LINE_SEP, this.context.getIndentationToken());
		buf.append(this.indent(1));
		buf.append("private String instanceName;");
		buf.append(LINE_SEP);        
		buf.append(this.indent(1));
		buf.append("private String description;");
		return buf.toString();
	}


}
