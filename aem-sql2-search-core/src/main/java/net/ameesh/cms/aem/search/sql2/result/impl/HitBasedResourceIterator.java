package net.ameesh.cms.aem.search.sql2.result.impl;

import net.akqa.aem.search.sql2.api.exception.QueryException;
import org.apache.sling.api.resource.Resource;

import javax.jcr.RepositoryException;
import java.util.Iterator;

/**
 * Created by ameesh.trikha on 2/23/16.
 */
public class HitBasedResourceIterator implements Iterator<Resource> {

    private final Iterator<com.day.cq.search.result.Hit> hits;

    public HitBasedResourceIterator(Iterator<com.day.cq.search.result.Hit> hits) {
        this.hits = hits;
    }

    @Override
    public boolean hasNext() {
        return hits.hasNext();
    }

    @Override
    public Resource next() {
        try {
            return hits.next().getResource();
        } catch (RepositoryException re) {
           throw new QueryException("Unable to get Resource from hit" , re);
        }
    }
}
