package net.ameesh.cms.aem.search.sql2.impl;

import com.day.cq.search.result.SearchResult;
import net.akqa.aem.search.sql2.api.Sql2Search;
import net.ameesh.cms.aem.search.sql2.query.impl.SQLQuery;
import net.akqa.aem.search.sql2.api.query.provider.QueryProvider;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;

/**
 * Created by ameesh.trikha on 2/22/16.
 */
@Component(metatype = true, label = "SQL2 Search Service",
        description = "Service to facilitate SQL2 query based search")
@Service
public class Sql2SearchService implements Sql2Search {

    /**
     * Adapts the sling request to QueryProvider to get the SQLQuery to execute
     * @param providerClass - provider class that adapts from Sling http request
     * @param slingHttpServletRequest - request
     * @return
     */
    public SearchResult doQuery(Class<? extends QueryProvider> providerClass, SlingHttpServletRequest slingHttpServletRequest) {
        final SQLQuery sqlQuery = (SQLQuery) slingHttpServletRequest.adaptTo(providerClass).getQuery();
        return sqlQuery.getResult();
    }
}
