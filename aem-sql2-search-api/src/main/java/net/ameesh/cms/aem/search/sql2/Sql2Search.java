package net.ameesh.cms.aem.search.sql2;

import com.day.cq.search.result.SearchResult;
import net.ameesh.cms.aem.search.sql2.query.provider.QueryProvider;
import org.apache.sling.api.SlingHttpServletRequest;

/**
 * Created by ameesh.trikha on 2/22/16.
 */
public interface Sql2Search {

    SearchResult doQuery(Class<? extends QueryProvider> providerClass, SlingHttpServletRequest slingHttpServletRequest);
}
