package net.ameesh.cms.aem.search.sql2.exception;

/**
 * Created by ameesh.trikha on 2/22/16.
 */
public class QueryException extends RuntimeException {

    public QueryException(String message) {
        super(message);
    }

    public QueryException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
