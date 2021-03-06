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
package org.plasma.model;



import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import junit.framework.Test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.modeldriven.fuml.repository.Class_;
import org.modeldriven.fuml.repository.Classifier;
import org.modeldriven.fuml.repository.Repository;
import org.modeldriven.fuml.repository.Property;
import org.modeldriven.fuml.xmi.XmiValidationEventHandler;
import org.plasma.common.test.PlasmaTest;
import org.plasma.common.test.PlasmaTestSetup;
import org.plasma.sdo.repository.PlasmaRepository;

import fUML.Library.ChannelImplementation.StandardOutputChannelObject;
import fUML.Syntax.Actions.BasicActions.CallBehaviorAction;
import fUML.Syntax.Activities.ExtraStructuredActivities.ExpansionRegion;
import fUML.Syntax.Activities.IntermediateActivities.Activity;
import fUML.Syntax.Activities.IntermediateActivities.ActivityEdge;
import fUML.Syntax.Activities.IntermediateActivities.ActivityParameterNode;
import fUML.Syntax.Activities.IntermediateActivities.ObjectNode;

import fUML.Syntax.Classes.Kernel.DataType;
import fUML.Syntax.Classes.Kernel.Feature;
import fUML.Syntax.Classes.Kernel.LiteralInteger;
import fUML.Syntax.Classes.Kernel.LiteralUnlimitedNatural;

import fUML.Syntax.Classes.Kernel.NamedElement;
import fUML.Syntax.Classes.Kernel.Parameter;
import fUML.Syntax.Classes.Kernel.TypedElement;
import fUML.Syntax.CommonBehaviors.BasicBehaviors.OpaqueBehavior;



/**
 * 
 */
public class ModelTestCase extends PlasmaTest {
    private static Log log = LogFactory.getLog(ModelTestCase.class);
    
    public static Test suite() {
        return PlasmaTestSetup.newTestSetup(ModelTestCase.class);
    }
    
    public void setUp() throws Exception {
    }

    public void testCreateModel() throws Exception {
    	
        log.info("testCreateModel");
        Repository model = PlasmaRepository.getInstance();
        log.info("returned an instance");
        
        //Object lock = new Object();
        //synchronized(lock) {
        //	lock.wait(300000);
        //}
        log.info("done");
    }
    
    public void testActivity() throws Exception {    
        Classifier activityClassifier = PlasmaRepository.getInstance().findClassifier(Activity.class.getSimpleName());
        assertTrue("could not find class 'Activity'", 
        		activityClassifier != null);
        
        
        log.info("done");
    }

    public void testActivityEdge() throws Exception {    
    	Class_ activityEdgeClassifier = (Class_)PlasmaRepository.getInstance().findClassifier(ActivityEdge.class.getSimpleName());
        assertTrue("could not find class 'ActivityEdge'", 
        		activityEdgeClassifier != null);
        Property isLeaf = activityEdgeClassifier.findProperty("isLeaf");
        assertTrue("No ownedAttribute 'isLeaf' found for class 'Activity'", isLeaf != null);
        assertTrue("Expected singular ownedAttribute 'isLeaf' for class 'Activity'", 
        		isLeaf.isSingular());
       log.info("done");
    }
    
    
    public void testOpaqueBehavior() throws Exception {    
    	Class_ opaqueBehaviorClassifier = (Class_)PlasmaRepository.getInstance().findClassifier(OpaqueBehavior.class.getSimpleName());
        assertTrue("could not find class 'OpaqueBehavior'", opaqueBehaviorClassifier != null);
        Property visibility = opaqueBehaviorClassifier.findProperty("visibility");
        assertTrue("No ownedAttribute 'visibility' found for class 'OpaqueBehavior'", visibility != null);
        log.info("done");
    }
    
    public void testOpaqueExpression() throws Exception {    
    	Class_ opaqueExpressionClassifier = (Class_)PlasmaRepository.getInstance().findClassifier("OpaqueExpression");
        assertTrue("could not find class 'OpaqueExpression'", opaqueExpressionClassifier != null);
        Property visibility = opaqueExpressionClassifier.findProperty("visibility");
        assertTrue("No ownedAttribute 'visibility' found for class 'OpaqueExpression'", visibility != null);
        log.info("done");
    }

    public void testParameter() throws Exception {    
        log.info("testParameter");
        Class_ parameter = (Class_)PlasmaRepository.getInstance().findClassifier(Parameter.class.getSimpleName());
        assertTrue("could not find class 'Parameter'", parameter != null);
        Property multiplicityElement = parameter.findProperty("multiplicityElement");
        assertTrue("No ownedAttribute 'multiplicityElement' expected for class 'Parameter'", multiplicityElement == null);
        Property direction = parameter.findProperty("direction");
        assertTrue("The ownedAttribute 'direction' expected for class 'Parameter'", direction != null);
        assertTrue("Expected 'Parameter.direction' as required", 
        		direction.isRequired());
        assertTrue("Expected default value for 'Parameter.direction'", 
        		direction.hasPropertyDefaultValue());
        
        log.info("done");
    }

    public void testCallBehaviorAction() throws Exception {    
        log.info("testCallBehaviorAction");
        Class_ actionClassifier = (Class_)PlasmaRepository.getInstance().findClassifier(CallBehaviorAction.class.getSimpleName());
        assertTrue("could not find classifier 'CallBehaviorAction'", actionClassifier != null);
        Property behavior = actionClassifier.findProperty("behavior");
        assertTrue("No ownedAttribute 'behavior' found for class 'CallBehaviorAction'", behavior != null);
        assertTrue("Expected ownedAttribute 'behavior' as required for class 'CallBehaviorAction'", 
        		behavior.isRequired());
        log.info("done");
    }

    public void testLiteralInteger() throws Exception {    
        log.info("testLiteralInteger");
        Class_ integerClassifier = (Class_)PlasmaRepository.getInstance().findClassifier(LiteralInteger.class.getSimpleName());
        assertTrue("could not find class for 'LiteralInteger'", integerClassifier != null);
        Property nameProp = integerClassifier.findProperty("name");
        assertTrue("No ownedAttribute 'name' found for class 'LiteralInteger'", nameProp != null);
        Classifier nameType = nameProp.findType();
        log.info("done");
    }

    public void testLiteralUnlimitedNatural() throws Exception {    
        log.info("testLiteralUnlimitedNatural");
        Class_ classifier = (Class_)PlasmaRepository.getInstance().findClassifier(LiteralUnlimitedNatural.class.getSimpleName());
        assertTrue("could not find class for 'LiteralUnlimitedNatural'", classifier != null);
        Property nameProp = classifier.findProperty("name");
        assertTrue("No ownedAttribute 'name' found for class 'LiteralUnlimitedNatural'", nameProp != null);
        Classifier nameType = nameProp.findType();
        assertTrue("LiteralUnlimitedNatural.name is not an instance of DataType", 
                nameType.getDelegate() instanceof DataType);
        Property valueProp = classifier.findProperty("value");
        assertTrue("No ownedAttribute 'value' found for class 'LiteralUnlimitedNatural'", valueProp != null);
        Classifier valueType = valueProp.findType();
        assertNotNull("LiteralUnlimitedNatural.value is null", 
        		valueType);
        log.info("done");
    }    

    public void testModel() throws Exception {    
        log.info("testModel");
        Classifier modelClassifier = PlasmaRepository.getInstance().findClassifier("Model");
        assertTrue("could not find class for 'Model'", modelClassifier != null);
        //Property profileApplicationProp = Model.getInstance().findAttribute((Class_)modelClassifier, "profileApplication");
        //assertTrue("No ownedAttribute 'profileApplication' found for class 'Model'", profileApplicationProp != null);
        log.info("done");
    }

    public void testTypedElement() throws Exception {    
        log.info("testTypedElement");
        Class_ typedElementClassifier = (Class_)PlasmaRepository.getInstance().findClassifier(TypedElement.class.getSimpleName());
        assertTrue("could not find class for 'TypedElement'", typedElementClassifier != null);
        Property type = typedElementClassifier.findProperty("type");
        assertTrue("No ownedAttribute 'type' found for class 'TypedElement'", type != null);
        log.info("done");
    }

    public void testNamedElement() throws Exception {    
        log.info("testTypedElement");
        Class_ namedElementClassifier = (Class_)PlasmaRepository.getInstance().findClassifier(NamedElement.class.getSimpleName());
        assertTrue("could not find class for 'NamedElement'", namedElementClassifier != null);
        Property name = namedElementClassifier.findProperty("name");
        assertTrue("No ownedAttribute 'name' found for class 'NamedElement'", name != null);
        Property visibility = namedElementClassifier.findProperty("visibility");
        assertTrue("No ownedAttribute 'visibility' found for class 'NamedElement'", visibility != null);
        log.info("done");
    }
    
    public void testFeature() throws Exception {    
        log.info("testFeature");
        Class_ featureClassifier = (Class_)PlasmaRepository.getInstance().findClassifier(Feature.class.getSimpleName());
        assertTrue("could not find class for 'Feature'", featureClassifier != null);
        Property isLeaf = featureClassifier.findProperty("isLeaf");
        assertTrue("No ownedAttribute 'isLeaf' found for class 'Feature'", isLeaf != null);
        log.info("done");
    }
    
    public void testActivityParameterNode() throws Exception {    
        log.info("testActivityParameterNode");
        Class_ nodeClassifier = (Class_)PlasmaRepository.getInstance().findClassifier(ActivityParameterNode.class.getSimpleName());
        assertTrue("could not find class for 'ActivityParameterNode'", nodeClassifier != null);
        Property type = nodeClassifier.findProperty("type");
        assertTrue("No ownedAttribute 'type' found for class 'ActivityParameterNode'", type != null);
        log.info("done");
    }
    
    public void testClass() throws Exception {    
        log.info("testClass");
        Class_ classifier = (Class_)PlasmaRepository.getInstance().findClassifier("Class");
        assertTrue("could not find class for 'Class'", classifier != null);
        Property isAbstract = classifier.findProperty("isAbstract");
        assertTrue("No ownedAttribute 'isAbstract' found for class 'Class'", isAbstract != null);
        assertTrue("Expected 'Class.isAbstract' as singular ownedAttribute", 
        		isAbstract.isSingular());
        assertTrue("Expected 'Class.isAbstract' as required ownedAttribute", 
        		isAbstract.isRequired());
        assertTrue("Expected default value for 'Class.isAbstract'", 
        		isAbstract.hasPropertyDefaultValue());
        Property isLeaf = classifier.findProperty("isLeaf");
        assertNull("Unexpected ownedAttribute 'isLeaf' found for class 'Class'", isLeaf);
        Property generalizationProp = classifier.findProperty("generalization");
        assertTrue("No ownedAttribute 'generalization' found for class 'Class'", generalizationProp != null);

        log.info("done");
    }

    public void testProperty() throws Exception {    
        log.info("testProperty");
        Class_ propertyClassifier = (Class_)PlasmaRepository.getInstance().findClassifier("Property");
        assertTrue("could not find class for 'Property'", propertyClassifier != null);
        logProperties((Class_)propertyClassifier);
        Property defaultValue = propertyClassifier.findProperty("defaultValue");
        assertTrue("No ownedAttribute 'defaultValue' found for class 'Property'", defaultValue != null);
        log.info("done");
    }
   
    
    private void logProperties(Class_ classifier) {
        List<Property> props = classifier.getAllProperties();
        for (Property prop : props) {
        	log.info(classifier.getName() + ": " + prop.getClass_().getName() + "." + prop.getName() 
        			+ " (required=" + String.valueOf(
        					prop.isRequired()) 
        				+ ")");
        	Property opposite = prop.getOpposite();
            if (opposite != null)
            	log.info(classifier.getName() + ": opposite: " + prop.getClass_().getName() + "." + prop.getName() + "->" 
            			+ opposite.getClass_().getName() + "." + opposite.getName());
        }      
    	
    }
    
    
    public void testObjectNode() throws Exception {    
        log.info("testObjectNode");
        Class_ objectNodeClassifier = (Class_)PlasmaRepository.getInstance().findClassifier(ObjectNode.class.getSimpleName());
        assertTrue("could not find classifier 'ObjectNode'", objectNodeClassifier != null);
        /*
        Property orderingProp = ModelAccess.INSTANCE.findAttribute((Class_)objectNodeClassifier, "ordering");
        assertTrue("No ownedAttribute 'ordering' found for class 'ObjectNode'", orderingProp != null);
        assertTrue("Expected 'ObjectNode.ordering' as required property",
            ModelAccess.INSTANCE.isRequired((Class_)objectNodeClassifier, orderingProp));
        String dflt = ModelAccess.INSTANCE.getAttributeDefault(orderingProp);
        assertTrue("no default found for 'ObjectNode.ordering'", 
                dflt != null);
        assertTrue("expected 'FIFO' as default for 'ObjectNode.ordering'", 
                "FIFO".equals(dflt));
        */
                       

        log.info("done");
    }
    
    public void testExpansionRegion() throws Exception {    
        log.info("testExpansionRegion");
        Class_ expansionRegionClassifier = (Class_)PlasmaRepository.getInstance().findClassifier(ExpansionRegion.class.getSimpleName());
        assertTrue("could not find classifier 'ExpansionRegion'", expansionRegionClassifier != null);
        Property modeProp = expansionRegionClassifier.findProperty("mode");
        assertTrue("No ownedAttribute 'mode' found for class 'ExpansionRegion'", modeProp != null);
        String dflt = modeProp.getPropertyDefault();
        
        assertTrue("Expected default for 'mode' property for class 'ExpansionRegion'", 
                dflt != null);
        log.info("done");
    }

    public void testPackage() throws Exception {    
        log.info("testClass");
        Class_ pkg = (Class_)PlasmaRepository.getInstance().findClassifier("Package");
        assertTrue("could not find class for 'Package'", pkg != null);

        Property nestingPackage = pkg.findProperty("nestingPackage");
        assertTrue("No ownedAttribute 'nestingPackage' found for class 'Package'", nestingPackage != null);
        Property nestingPackageOpposite = nestingPackage.getOpposite();
        assertTrue(nestingPackageOpposite != null);
        assertTrue(nestingPackageOpposite.getClass_().getName().equals("Package"));
        assertTrue(nestingPackageOpposite.getName().equals("nestedPackage"));
        
        List<Property> props = pkg.getAllProperties();
        for (Property prop : props) {
        	Property opposite = prop.getOpposite();
            if (opposite != null)
            	log.info(prop.getClass_().getName() + "." + prop.getName() + "->" 
            			+ opposite.getClass_().getName() + "." + opposite.getName());
        }
        
        log.info("done");
    }
    
}