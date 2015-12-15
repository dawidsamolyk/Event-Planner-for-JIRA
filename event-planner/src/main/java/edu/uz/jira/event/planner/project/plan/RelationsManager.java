package edu.uz.jira.event.planner.project.plan;

import com.atlassian.activeobjects.external.ActiveObjects;
import edu.uz.jira.event.planner.project.plan.model.Component;
import edu.uz.jira.event.planner.project.plan.model.Domain;
import edu.uz.jira.event.planner.project.plan.model.Plan;
import edu.uz.jira.event.planner.project.plan.model.relation.PlanToComponentRelation;
import edu.uz.jira.event.planner.project.plan.model.relation.PlanToDomainRelation;
import net.java.ao.Query;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Manages database Entities (Active Objects) relations.
 */
class RelationsManager {
    private final ActiveObjects activeObjectsService;

    /**
     * Constructor.
     *
     * @param activeObjectsService Injected {@code ActiveObjects} implementation.
     */
    RelationsManager(@Nonnull final ActiveObjects activeObjectsService) {
        this.activeObjectsService = activeObjectsService;
    }

    PlanToDomainRelation associate(@Nonnull final Plan plan, @Nonnull final Domain domain) {
        if (plan == null || domain == null) {
            return null;
        }
        PlanToDomainRelation result = activeObjectsService.create(PlanToDomainRelation.class);
        result.setDomain(domain);
        result.setPlan(plan);
        result.save();
        return result;
    }

    private PlanToComponentRelation associate(@Nonnull final Plan plan, @Nonnull final Component component) {
        if (plan == null || component == null) {
            return null;
        }
        PlanToComponentRelation result = activeObjectsService.create(PlanToComponentRelation.class);
        result.setPlan(plan);
        result.setComponent(component);
        result.save();
        return result;
    }

    Collection<PlanToComponentRelation> associatePlanWithComponents(@Nonnull final Plan plan, @Nonnull final String[] componentsNames) {
        Collection<PlanToComponentRelation> result = new ArrayList<PlanToComponentRelation>();

        List<Component> components = new ArrayList<Component>();
        if (componentsNames != null && componentsNames.length > 0) {
            for (String eachDomainName : componentsNames) {
                Component[] eachComponent = activeObjectsService.find(Component.class, Query.select().where(Component.NAME + " = ?", eachDomainName));
                components.addAll(Arrays.asList(eachComponent));
            }
        }
        if (components != null && components.size() > 0 && plan != null) {
            for (Component eachComponent : components) {
                PlanToComponentRelation eachRelation = associate(plan, eachComponent);
                result.add(eachRelation);
            }
        }

        return result;
    }

    Collection<PlanToDomainRelation> associatePlanWithDomains(@Nonnull final Plan plan, @Nonnull final String[] domainsNames) {
        Collection<PlanToDomainRelation> result = new ArrayList<PlanToDomainRelation>();

        List<Domain> domains = new ArrayList<Domain>();
        if (domainsNames != null && domainsNames.length > 0) {
            for (String eachDomainName : domainsNames) {
                Domain[] eachDomains = activeObjectsService.find(Domain.class, Query.select().where(Domain.NAME + " = ?", eachDomainName));
                domains.addAll(Arrays.asList(eachDomains));
            }
        }
        if (domains != null && domains.size() > 0 && plan != null) {
            for (Domain eachDomain : domains) {
                PlanToDomainRelation eachRelation = associate(plan, eachDomain);
                result.add(eachRelation);
            }
        }
        return result;
    }
}
