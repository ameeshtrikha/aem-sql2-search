package net.ameesh.cms.aem.search.sql2.result.impl;

import com.day.cq.search.result.Hit;
import net.akqa.aem.search.sql2.api.exception.QueryException;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Iterator;

/**
 * Created by ameesh.trikha on 2/23/16.
 */
public class HitBasedNodeIterator implements Iterator<Node> {

    private final Iterator<com.day.cq.search.result.Hit> hits;

    public HitBasedNodeIterator(Iterator<Hit> hits) {
        this.hits = hits;
    }


    @Override
    public boolean hasNext() {
        return this.hits.hasNext();
    }

    @Override
    public Node next() {

        try {
            return this.hits.next().getNode();
        } catch (RepositoryException re) {
            throw new QueryException("Error while getting node from hit ", re);
        }
    }
}
