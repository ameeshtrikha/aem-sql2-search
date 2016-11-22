package net.ameesh.cms.aem.search.sql2.query;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.eval.PredicateEvaluator;

/**
 * Created by ameesh.trikha on 2/22/16.
 */
public interface Query extends com.day.cq.search.Query {

    default PredicateGroup getPredicates() {
        throw new UnsupportedOperationException("This operation is not supported");
    }

    default void registerPredicateEvaluator(String paramString, PredicateEvaluator paramPredicateEvaluator) {
        throw new UnsupportedOperationException("This operation is not supported");
    }

}
