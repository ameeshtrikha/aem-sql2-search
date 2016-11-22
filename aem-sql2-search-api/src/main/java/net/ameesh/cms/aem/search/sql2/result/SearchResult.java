package net.ameesh.cms.aem.search.sql2.result;

import com.day.cq.search.result.ResultPage;

import java.util.List;

/**
 * Created by ameesh.trikha on 2/22/16.
 */
public interface SearchResult extends com.day.cq.search.result.SearchResult{

    default List<ResultPage> getResultPages() {
        throw new UnsupportedOperationException("This operation is not supported");
    }

    default ResultPage getPreviousPage() {
        throw new UnsupportedOperationException("This operation is not supported");
    }

    default ResultPage getNextPage() {
        throw new UnsupportedOperationException("This operation is not supported");
    }

}
