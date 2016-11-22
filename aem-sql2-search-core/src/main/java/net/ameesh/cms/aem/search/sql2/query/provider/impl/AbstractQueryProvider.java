package net.ameesh.cms.aem.search.sql2.query.provider.impl;

import com.day.cq.search.Predicate;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import net.akqa.aem.search.sql2.api.facets.FacetPredicateEvaluator;
import net.akqa.aem.search.sql2.api.facets.PredicateEvaluatorWrapper;
import net.akqa.aem.search.sql2.api.facets.QueryFacetPredicateProvider;
import net.akqa.aem.search.sql2.api.query.provider.QueryProvider;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * Created by ameesh.trikha on 2/23/16.
 */
public abstract class AbstractQueryProvider implements QueryProvider {

    static String QUERY_SELECT = "select * from [cq:Page] where %s order by %s";
    static String DEFAULT_ORDER_BY = "[jcr:content/cq:lastModified] desc";
    static String IS_DECENDENT_NODE = "isdescendantnode('%s')";
    static String FULL_TEXT = "CONTAINS(*, '%s')";
    static String TAG_SEARCH = "[jcr:content/cq:tags] in (%s)";

    // Used for Full text search
    String queryString;
    String [] searchPaths;
    String [] filterTags;
    String sql2Query;
    List<Predicate> facetPredicates;
    List<PredicateEvaluatorWrapper> evaluators;
    private final QueryFacetPredicateProvider facetProvider;
    private final FacetPredicateEvaluator facetPredicateEvaluator;

    public AbstractQueryProvider(QueryFacetPredicateProvider facetProvider, FacetPredicateEvaluator facetPredicateEvaluator) {
        this.facetProvider = facetProvider;
        this.facetPredicateEvaluator = facetPredicateEvaluator;
    }


    protected String buildQueryConditions() {
        StringBuilder whereClause = new StringBuilder();
        if (searchPaths.length > 1) {
            whereClause.append("( ");
            for (int count = 0; count < searchPaths.length; count++) {
                whereClause.append(format(IS_DECENDENT_NODE, searchPaths[count]));
                if (count < searchPaths.length - 1) {
                    whereClause.append(" or ");
                }
            }
            whereClause.append(" )");
        } else {
            whereClause.append(format(IS_DECENDENT_NODE, searchPaths[0]));
        }

        if (StringUtils.isNotEmpty(queryString)) {
            whereClause.append(" and ");
            whereClause.append(format(FULL_TEXT, queryString));
        }

        if (null != filterTags && filterTags.length > 0) {
            whereClause.append(" and ");
            whereClause.append(format(TAG_SEARCH, processTags()));
        }


        return whereClause.toString();
    }

    protected String processTags() {

        Function<String, String> addQuotes = s ->  new StringBuilder(s.length() + 2).append("'").append(s).append("'").toString();

        return Joiner.on(", ").join(Iterables.transform(Arrays.asList(filterTags), addQuotes));
    }

    /**
     * This method is used to extract/process tags sent in as comma or pipe separated values in URL.
     * @param queryTag - tags string extracted as query parameter from request.
     * @return
     */
    protected String [] processTagsInRequestParam(String queryTag) {
        String[] tags;
        if (queryTag.split(",").length > 1) {
            tags = queryTag.split(",");
        } else {
            tags = queryTag.split("\\|");
        }

        return tags;
    }

    protected List<PredicateEvaluatorWrapper> getEvaluators() {
        this.facetPredicates = this.facetProvider.getFacetPredicates(this.getClass().getName());
        if (null != facetPredicates && facetPredicates.size() > 0) {
            evaluators = facetPredicates.stream()
                    .map(predicate -> facetPredicateEvaluator.getPredicateEvaluator(predicate))
                    .collect(Collectors.toList());
        }

        return evaluators;
    }

}
