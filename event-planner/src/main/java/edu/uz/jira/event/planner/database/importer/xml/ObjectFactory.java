//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.01.08 at 06:58:36 PM CET 
//

package edu.uz.jira.event.planner.database.importer.xml;

import edu.uz.jira.event.planner.database.importer.xml.model.*;

import javax.xml.bind.annotation.XmlRegistry;

/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the generated package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 */
@XmlRegistry
public class ObjectFactory {

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AllEventPlans }
     */
    public AllEventPlans createEventPlans() {
        return new AllEventPlans();
    }

    /**
     * Create an instance of {@link EventPlan }
     */
    public EventPlan createEventPlansEventPlan() {
        return new EventPlan();
    }

    /**
     * Create an instance of {@link Component }
     */
    public Component createEventPlansEventPlanComponent() {
        return new Component();
    }

    /**
     * Create an instance of {@link Task }
     */
    public Task createEventPlansEventPlanComponentTask() {
        return new Task();
    }

    /**
     * Create an instance of {@link Domain }
     */
    public Domain createEventPlansEventPlanDomain() {
        return new Domain();
    }

    /**
     * Create an instance of {@link SubTask }
     */
    public SubTask createEventPlansEventPlanComponentTaskSubTask() {
        return new SubTask();
    }

}