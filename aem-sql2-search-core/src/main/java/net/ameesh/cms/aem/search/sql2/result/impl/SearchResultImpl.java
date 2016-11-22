package net.ameesh.cms.aem.search.sql2.result.impl;

import com.day.cq.search.facets.Facet;
import com.day.cq.search.result.Hit;
import com.google.common.collect.Lists;
import net.akqa.aem.search.sql2.api.result.SearchResult;
import net.ameesh.cms.aem.search.sql2.query.impl.SQLQuery;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import java.time.Duration;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by ameesh.trikha on 2/22/16.
 */
public class SearchResultImpl implements SearchResult {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchResultImpl.class);
    private final SQLQuery query;
    private final long executionTime;
    private final long totalMatches;
    private final long startIndex;
    private final long hitsPerPage;
    private final List<Hit> hits;


    public SearchResultImpl(SQLQuery sqlQuery, NodeIterator nodeIterator, long executionTime) {

        this.query = sqlQuery;
        this.executionTime = executionTime;
        this.hitsPerPage = sqlQuery.getHitsPerPage();

        try {
            nodeIterator.skip(sqlQuery.getStart());
        } catch (NoSuchElementException nsee) {
            LOGGER.debug("Error while moving to start index, defaulting to base", nsee);
        }
        this.startIndex = nodeIterator.getPosition();

        List<Node> nodeList = Lists.newArrayList(nodeIterator);
        this.hits = Collections.unmodifiableList(IntStream.range(0, nodeList.size())
                .mapToObj(i -> new HitImpl(sqlQuery, nodeList.get(i), this.startIndex + i))
                .collect(Collectors.toList()));

        if (nodeIterator.getSize() < 0L) {
            this.totalMatches = (this.startIndex + hits.size());
        } else {
            this.totalMatches = nodeIterator.getSize();
        }

    }

    @Override
    public long getTotalMatches() {
        return this.totalMatches;
    }

    @Override
    public boolean hasMore() {
        return false;
    }

    @Override
    public long getStartIndex() {
        return this.startIndex;
    }

    @Override
    public long getHitsPerPage() {
        return this.hitsPerPage;
    }

    @Override
    public List<Hit> getHits() {
        return this.hits;
    }

    @Override
    public Iterator<Node> getNodes() {
        return new HitBasedNodeIterator(this.hits.iterator());
    }

    @Override
    public Iterator<Resource> getResources() {
        return new HitBasedResourceIterator(this.hits.iterator());
    }

    @Override
    public String getExecutionTime() {
        return Long.valueOf(Duration.ofMillis(executionTime).getSeconds()).toString();
    }

    @Override
    public long getExecutionTimeMillis() {
        return this.executionTime;
    }

    @Override
    public Map<String, Facet> getFacets() throws RepositoryException {
        return this.query.extractFacets();
    }

    @Override
    public String getQueryStatement() {
        return this.query.getSql2Query();
    }

    @Override
    public String getFilteringPredicates() {
        return null;
    }

}
