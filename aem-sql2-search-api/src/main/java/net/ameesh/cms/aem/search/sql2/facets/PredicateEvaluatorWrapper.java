package net.ameesh.cms.aem.search.sql2.facets;

import com.day.cq.search.Predicate;
import com.day.cq.search.eval.PredicateEvaluator;

/**
 * Created by ameesh.trikha on 2/24/16.
 */
public class PredicateEvaluatorWrapper {

    private final PredicateEvaluator predicateEvaluator;
    private final Predicate predicate;

    public PredicateEvaluatorWrapper(PredicateEvaluator predicateEvaluator, Predicate predicate) {
        this.predicateEvaluator = predicateEvaluator;
        this.predicate = predicate;
    }

    public PredicateEvaluator getPredicateEvaluator() {
        return predicateEvaluator;
    }

    public Predicate getPredicate() {
        return predicate;
    }
}
