package net.ameesh.cms.aem.search.sql2.query.impl;

import com.day.cq.search.Predicate;
import com.day.cq.search.eval.EvaluationContext;
import com.day.cq.search.eval.PredicateEvaluator;
import com.day.cq.search.facets.Bucket;
import com.day.cq.search.facets.Facet;
import com.day.cq.search.facets.FacetExtractor;
import com.day.cq.search.result.SearchResult;
import com.google.common.collect.Lists;
import net.akqa.aem.search.sql2.api.exception.QueryException;
import net.akqa.aem.search.sql2.api.facets.PredicateEvaluatorWrapper;
import net.akqa.aem.search.sql2.api.query.Query;
import net.akqa.aem.search.sql2.api.query.QueryFilter;
import net.ameesh.cms.aem.search.sql2.result.impl.SearchResultImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.commons.iterator.NodeIteratorAdapter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.query.Row;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by ameesh.trikha on 2/22/16.
 */
public class SQLQuery implements Query {

    private String sql2Query;
    private final List<QueryFilter> filters;
    private final Session session;
    private QueryResult rawQueryResult;
    private long start;
    private long hitsPerPage;
    private final ResourceResolver resourceResolver;
    private final List<PredicateEvaluatorWrapper> evaluators;
    private Map<FacetExtractor, List<Predicate>> extractors;
    private SearchResultImpl searchResult;

    private static final Logger LOGGER = LoggerFactory.getLogger(SQLQuery.class);

    public SQLQuery(ResourceResolver resourceResolver, List<QueryFilter> filters, String simpleSqlQuery, List<PredicateEvaluatorWrapper> evaluators) {
        this.filters = filters != null ? filters : new ArrayList<>();
        this.session = resourceResolver.adaptTo(Session.class);
        this.start = 0;
        this.hitsPerPage = Long.MAX_VALUE;
        this.sql2Query = simpleSqlQuery;
        this.resourceResolver = resourceResolver;
        this.evaluators = evaluators;
    }

    @Override
    public SearchResult getResult() {

        if (StringUtils.isEmpty(sql2Query)) {
            throw new QueryException("Query not specified for execution");
        }

        try {
            long time = System.currentTimeMillis();
            final QueryManager queryManager = session.getWorkspace().getQueryManager();
            rawQueryResult = queryManager.createQuery(sql2Query, javax.jcr.query.Query.JCR_SQL2).execute();

            if (filters.size() > 0) {

                final List<Node> filteredNodes = (List<Node>) Lists.newArrayList(rawQueryResult.getNodes())
                        .parallelStream()
                        .filter(node -> filters.stream()
                                .allMatch(filter -> filter.test((Node) node)))
                        .collect(Collectors.toList());

                searchResult = new SearchResultImpl(this, new NodeIteratorAdapter(filteredNodes.iterator()), System.currentTimeMillis() - time);
            } else {

                searchResult = new SearchResultImpl(this, rawQueryResult.getNodes(), System.currentTimeMillis() - time);
            }

            collectFacetExtractors();

        } catch (RepositoryException re) {
            throw new QueryException("Error while getting results", re);
        }

        return searchResult;

    }

    @Override
    public com.day.cq.search.Query refine(Bucket bucket) {
        throw new UnsupportedOperationException("Yet to be implemented");
    }

    @Override
    public void setExcerpt(boolean b) {

    }

    @Override
    public boolean getExcerpt() {
        return false;
    }

    @Override
    public void setStart(long start) {
        this.start = start;
    }

    @Override
    public long getStart() {
        return this.start;
    }

    @Override
    public void setHitsPerPage(long hitsPerPage) {
        this.hitsPerPage = hitsPerPage;
    }

    @Override
    public long getHitsPerPage() {
        return this.hitsPerPage;
    }

    /**
     * Query String in scope.
     *
     * @return
     */
    public String getSql2Query() {
        return sql2Query;
    }

    public ResourceResolver getResourceResolver() {
        return this.resourceResolver;
    }

    private void collectFacetExtractors() {
        extractors = new HashMap<>();
        EvaluationContext context = new ProxyEvalutationContext(getResourceResolver());
        if (this.evaluators != null && this.evaluators.size() > 0) {
            for (PredicateEvaluatorWrapper evaluator : evaluators) {
                FacetExtractor extractor = evaluator.getPredicateEvaluator().getFacetExtractor(evaluator.getPredicate(), context);
                if (extractor != null)
                    if (extractors.containsKey(extractor)) {
                        ((List) extractors.get(extractor)).add(evaluator.getPredicate());
                    } else {
                        List predicates = new ArrayList();
                        predicates.add(evaluator.getPredicate());
                        extractors.put(extractor, predicates);
                    }
            }
        }
    }

    /**
     *
     * @return
     */
    public Map<String, Facet> extractFacets() {
        long time = System.currentTimeMillis();

        Map<String, Facet> facets = new HashMap();

        if (this.extractors.size() == 0) {
            return facets;
        }
        Iterator<Node> nodeIterator = this.searchResult.getNodes();
        nodeIterator
                .forEachRemaining(node -> this.extractors
                        .keySet()
                        .stream()
                        .forEach(e -> processFacetsOnNode(e, node)));

        for (Map.Entry<FacetExtractor,List<Predicate>> entry: this.extractors.entrySet()) {
            List<Predicate> predicates = entry.getValue();
            Facet facet = entry.getKey().getFacet();
            if (facet != null)
                for (Predicate p : predicates)
                    facets.put(p.getPath(), facet);
        }
        LOGGER.debug("facet extraction took {} ms", Long.valueOf(System.currentTimeMillis() - time));

        return facets;
    }

    private void processFacetsOnNode(FacetExtractor fe, Node node) {

        try {
            fe.handleNode(node);
        } catch (RepositoryException re) {
            throw new QueryException("Error extracting facet ", re);
        }
    }

    class ProxyEvalutationContext implements com.day.cq.search.eval.EvaluationContext {

        private final ResourceResolver resourceResolver;

        public ProxyEvalutationContext(ResourceResolver resourceResolver) {
            this.resourceResolver = resourceResolver;
        }

        @Override
        public PredicateEvaluator getPredicateEvaluator(String s) {
            return null;
        }

        @Override
        public Session getSession() {
            return resourceResolver.adaptTo(Session.class);
        }

        @Override
        public ResourceResolver getResourceResolver() {
            return resourceResolver;
        }

        @Override
        public Node getNode(Row row) {
            return null;
        }

        @Override
        public String getPath(Row row) {
            return null;
        }

        @Override
        public Resource getResource(Row row) {
            return null;
        }

        @Override
        public void put(String s, Object o) {

        }

        @Override
        public Object get(String s) {
            return null;
        }
    }
}
