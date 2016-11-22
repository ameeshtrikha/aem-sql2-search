package net.ameesh.cms.aem.search.sql2.facets;

import com.day.cq.search.Predicate;

import java.util.List;

/**
 * Created by ameesh.trikha on 2/24/16.
 */
public interface QueryFacetPredicateProvider {

    List<Predicate> getFacetPredicates(String providerClassName);
}
