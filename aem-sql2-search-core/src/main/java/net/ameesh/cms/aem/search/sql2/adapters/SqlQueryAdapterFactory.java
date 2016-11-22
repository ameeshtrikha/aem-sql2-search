package net.ameesh.cms.aem.search.sql2.adapters;

import net.akqa.aem.search.sql2.api.facets.FacetPredicateEvaluator;
import net.akqa.aem.search.sql2.api.facets.QueryFacetPredicateProvider;
import net.ameesh.cms.aem.search.sql2.query.provider.impl.FullTextSearchQueryProvider;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.adapter.AdapterFactory;

/**
 * Created by ameesh.trikha on 2/23/16.
 */
@Component(metatype = true, label = "Adapter Factory for adapting to SqlQuery",
        description = "Component which setups full text search result component's query.")
@Service
@Properties({
        @Property(name = AdapterFactory.ADAPTABLE_CLASSES, value = "org.apache.sling.api.SlingHttpServletRequest", propertyPrivate = true),
        @Property(name = AdapterFactory.ADAPTER_CLASSES, value = "net.ameesh.cms.aem.search.sql2.query.provider.impl.FullTextSearchQueryProvider", propertyPrivate = true)})

public class SqlQueryAdapterFactory implements AdapterFactory {

    @Reference
    private QueryFacetPredicateProvider facetProvider;
    @Reference
    private FacetPredicateEvaluator facetPredicateEvaluator;

    @Override
    public <AdapterType> AdapterType getAdapter(Object adaptable, Class<AdapterType> type) {
        if (type == FullTextSearchQueryProvider.class && adaptable instanceof SlingHttpServletRequest) {
            final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) adaptable;
            final FullTextSearchQueryProvider provider = new FullTextSearchQueryProvider(slingRequest, this.facetProvider, this.facetPredicateEvaluator);
            return (AdapterType) provider;
        }
        return null;
    }
}
