package net.ameesh.cms.aem.search.sql2.query.provider.impl;

import net.akqa.aem.search.sql2.api.facets.FacetPredicateEvaluator;
import net.akqa.aem.search.sql2.api.facets.QueryFacetPredicateProvider;
import net.akqa.aem.search.sql2.api.query.Query;
import net.ameesh.cms.aem.search.sql2.query.impl.SQLQuery;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;

import static java.lang.String.format;

/**
 * Created by ameesh.trikha on 2/23/16.
 */
public class FullTextSearchQueryProvider extends AbstractQueryProvider {

    private String orderBy;

    private final SlingHttpServletRequest request;
    public FullTextSearchQueryProvider(SlingHttpServletRequest slingHttpServletRequest, QueryFacetPredicateProvider facetProvider, FacetPredicateEvaluator facetPredicateEvaluator) {
        super(facetProvider, facetPredicateEvaluator);
        this.request = slingHttpServletRequest;
        this.queryString = slingHttpServletRequest.getParameter("q");
        ValueMap vm = request.getResource().adaptTo(ValueMap.class);
        this.searchPaths = new String [] {MapUtils.getString(vm,"searchIn","/content")};
    }

    @Override
    public Query getQuery() {

        String whereClause = buildQueryConditions();
        orderBy = StringUtils.isEmpty(orderBy) ? DEFAULT_ORDER_BY : orderBy;
        sql2Query = format(QUERY_SELECT, whereClause, orderBy);

        return new SQLQuery(this.request.getResourceResolver(),null, sql2Query, getEvaluators());
    }
}
