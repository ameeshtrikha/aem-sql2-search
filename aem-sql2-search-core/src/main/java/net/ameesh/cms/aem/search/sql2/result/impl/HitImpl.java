package net.ameesh.cms.aem.search.sql2.result.impl;

import net.ameesh.cms.aem.search.sql2.query.impl.SQLQuery;
import net.akqa.aem.search.sql2.api.result.Hit;
import org.apache.commons.collections.MapUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Map;

/**
 * Created by ameesh.trikha on 2/22/16.
 */
public class HitImpl implements Hit {

    private final SQLQuery query;
    private final Node node;
    private final long index;

    public HitImpl(SQLQuery sqlQuery, Node node, long index) {
        this.query = sqlQuery;
        this.node = node;
        this.index = index;
    }

    @Override
    public long getIndex() {
        return this.index;
    }

    @Override
    public Map<String, String> getExcerpts() throws RepositoryException {
        throw new UnsupportedOperationException("Yet to be implemented");
    }

    @Override
    public String getExcerpt() throws RepositoryException {
        throw new UnsupportedOperationException("Yet to be implemented");
    }

    @Override
    public Resource getResource() throws RepositoryException {
        return this.query.getResourceResolver().getResource(this.node.getPath());
    }

    @Override
    public Node getNode() throws RepositoryException {
        return this.node;
    }

    @Override
    public String getPath() throws RepositoryException {
        return this.node.getPath();
    }

    @Override
    public ValueMap getProperties() throws RepositoryException {
        return getResource().adaptTo(ValueMap.class);
    }

    @Override
    public String getTitle() throws RepositoryException {
        return MapUtils.getString(getProperties(),"jcr:title", this.node.getName());
    }

    @Override
    public double getScore() throws RepositoryException {
        throw new UnsupportedOperationException("Yet to be implemented");
    }
}
