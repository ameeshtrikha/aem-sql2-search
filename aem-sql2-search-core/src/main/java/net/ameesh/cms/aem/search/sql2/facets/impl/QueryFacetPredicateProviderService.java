package net.ameesh.cms.aem.search.sql2.facets.impl;

import com.day.cq.search.Predicate;
import net.akqa.aem.search.sql2.api.facets.QueryFacetConfigurationFactory;
import net.akqa.aem.search.sql2.api.facets.QueryFacetPredicateProvider;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.References;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ameesh.trikha on 2/24/16.
 */
@Component(label = "Query Facets Provider", description = "Provides query facets from the QueryFacetConfiguration", immediate = true, metatype = false, enabled = true)
@Service
@References({@Reference(referenceInterface = QueryFacetConfigurationFactory.class,
        cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE,
        policy = ReferencePolicy.DYNAMIC, name = "FacetConfigurationFactory")})
public class QueryFacetPredicateProviderService implements QueryFacetPredicateProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryFacetPredicateProviderService.class);

    private Map<String, QueryFacetConfigurationFactory> factoryConfigs;

    protected synchronized void bindFacetConfigurationFactory(final QueryFacetConfigurationFactory config) {
        if (factoryConfigs == null) {
            factoryConfigs = new HashMap<>();
        }

        LOGGER.debug("Adding facets configuration for provider " + config.getQueryClass());

        factoryConfigs.put(config.getQueryClass(), config);
    }

    protected synchronized void unbindFacetConfigurationFactory(final QueryFacetConfigurationFactory config) {
        LOGGER.debug("Removing facets configuration for provider " + config.getQueryClass());
        factoryConfigs.remove(config.getQueryClass());
    }

    /**
     *
     * @param providerClassName
     * @return
     */
    public List<Predicate> getFacetPredicates(String providerClassName) {

        String [] configuredFacets = factoryConfigs.get(providerClassName).getFacets();
        List<Predicate> facetPredicates = new ArrayList<>();

        for(String facet : configuredFacets) {

            if(facet.split("#").length > 1) {
                Predicate facetPredicate = new Predicate(facet.split("#")[0],facet.split("#")[1]);
                facetPredicate.set("property",facet.split("#")[2]);
                facetPredicates.add(facetPredicate);
            } else {
                LOGGER.debug("Configuration not properly set for facet {}",facet);
            }
        }

        return facetPredicates;
}
}
