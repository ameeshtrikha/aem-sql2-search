package net.akqa.aem.search.component.slingmodel;

import com.day.cq.search.result.SearchResult;
import net.akqa.aem.search.sql2.api.Sql2Search;
import net.ameesh.cms.aem.search.sql2.query.provider.impl.FullTextSearchQueryProvider;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Created by ameesh.trikha on 2/23/16.
 */
@Model(adaptables = {SlingHttpServletRequest.class})
public class SearchResultSlingModel {

    @SlingObject
    private Resource resource;
    @SlingObject
    private SlingHttpServletRequest request;
    @Inject
    private Sql2Search sql2Search;

    private SearchResult searchResult;


    @PostConstruct
    public void init() {
        searchResult = sql2Search.doQuery(FullTextSearchQueryProvider.class, request);
    }

    public SearchResult getSearchResult() {
        return searchResult;
    }
}
