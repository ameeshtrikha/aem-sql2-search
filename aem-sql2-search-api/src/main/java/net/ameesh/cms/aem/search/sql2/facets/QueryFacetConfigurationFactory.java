package net.ameesh.cms.aem.search.sql2.facets;

/**
 * Created by ameesh.trikha on 2/24/16.
 */
public interface QueryFacetConfigurationFactory {

    String getQueryClass();

    String[] getFacets();
}
