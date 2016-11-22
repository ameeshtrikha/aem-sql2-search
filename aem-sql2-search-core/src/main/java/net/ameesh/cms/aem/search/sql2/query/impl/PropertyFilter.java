package net.ameesh.cms.aem.search.sql2.query.impl;

import net.akqa.aem.search.sql2.api.query.QueryFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * Created by ameesh.trikha on 2/22/16.
 */
public class PropertyFilter implements QueryFilter {

    private static Logger LOGGER = LoggerFactory.getLogger(QueryFilter.class);

    private String propertyName;
    private String propertyValue;

    public PropertyFilter(String propertyName, String propertyValue) {
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
    }

    @Override
    public boolean test(Node node) {

        try {
            if (node.hasProperty(propertyName) && node.getProperty(propertyName).getValue().getString().equals(propertyValue)) {
                return true;
            }
        } catch (RepositoryException re) {
            LOGGER.error("Exception while Evaluating the result filter, will return false", re);
            return false;
        }

        return false;
    }
}
