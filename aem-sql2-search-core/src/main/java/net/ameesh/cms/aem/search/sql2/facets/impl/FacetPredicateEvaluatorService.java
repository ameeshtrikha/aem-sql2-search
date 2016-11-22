package net.ameesh.cms.aem.search.sql2.facets.impl;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Deactivate;
import com.day.cq.search.Predicate;
import com.day.cq.search.eval.PredicateEvaluator;
import net.akqa.aem.search.sql2.api.facets.FacetPredicateEvaluator;
import net.akqa.aem.search.sql2.api.facets.PredicateEvaluatorWrapper;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.ComponentInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.String.format;

/**
 * Created by ameesh.trikha on 2/24/16.
 */

@Service
@Component
public class FacetPredicateEvaluatorService implements FacetPredicateEvaluator {

    private BundleContext bundleContext;
    private Map<String, ServiceReference> serviceReferenceMap = new ConcurrentHashMap<>();
    private Map<String, ComponentInstance> componentInstanceMap = new ConcurrentHashMap();
    private static final String NAME_PREDICATE_EVALUATOR_COMPONENT = "(component.factory="+PredicateEvaluator.class.getName()+"/%s)";
    private static final Logger LOGGER = LoggerFactory.getLogger(PredicateEvaluator.class);

    @Activate
    protected void activate(ComponentContext context) {
        this.bundleContext = context.getBundleContext();
    }

    @Deactivate
    protected void deactivate(ComponentContext context) {

        synchronized (this.serviceReferenceMap) {
            for(Map.Entry<String,ComponentInstance> componentInstance : this.componentInstanceMap.entrySet()) {
                componentInstance.getValue().dispose();
            }

            for(Map.Entry<String, ServiceReference> serviceReference : this.serviceReferenceMap.entrySet()) {
                this.bundleContext.ungetService(serviceReference.getValue());
            }
        }
        this.componentInstanceMap.clear();
        this.serviceReferenceMap.clear();
        this.bundleContext = null;
    }


    public PredicateEvaluatorWrapper getPredicateEvaluator(Predicate predicate) {

        ServiceReference [] serviceReferences;

        String serviceIdentifier = format(NAME_PREDICATE_EVALUATOR_COMPONENT, predicate.getType());
        if (this.componentInstanceMap.containsKey(serviceIdentifier)) {
            final ComponentInstance componentInstance = this.componentInstanceMap.get(serviceIdentifier);
            return new PredicateEvaluatorWrapper((PredicateEvaluator) componentInstance.getInstance(), predicate);
        }

        try {
            serviceReferences = this.bundleContext.getServiceReferences(ComponentFactory.class.getName(), serviceIdentifier);
        } catch (InvalidSyntaxException ise) {

            LOGGER.error("Could'nt get Predicate Evaluator for {}", serviceIdentifier, ise);
            return null;
        }

        if (null != serviceReferences && serviceReferences.length > 0) {
            final ServiceReference serviceReference = serviceReferences[0];
            final ComponentFactory componentFactory = (ComponentFactory) this.bundleContext.getService(serviceReference);
            final ComponentInstance componentInstance = componentFactory.newInstance(null);
            final PredicateEvaluator predicateEvaluator = (PredicateEvaluator) componentInstance.getInstance();
            if (predicateEvaluator == null) {
                LOGGER.error("Unable to get " + serviceIdentifier);
                componentInstance.dispose();
                this.bundleContext.ungetService(serviceReference);
            } else {
                synchronized (this.serviceReferenceMap) {
                    this.serviceReferenceMap.put(serviceIdentifier, serviceReference);
                    this.componentInstanceMap.put(serviceIdentifier, componentInstance);
                }
            }

            return new PredicateEvaluatorWrapper(predicateEvaluator , predicate);
        }

        return null;
    }

}
