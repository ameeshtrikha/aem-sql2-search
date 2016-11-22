package net.ameesh.cms.aem.search.sql2.facets;

import com.day.cq.search.Predicate;

/**
 * Created by ameesh.trikha on 2/24/16.
 */
public interface FacetPredicateEvaluator {

    PredicateEvaluatorWrapper getPredicateEvaluator(Predicate predicate);
}
