package net.ameesh.cms.aem.search.sql2.query;

import javax.jcr.Node;
import java.util.function.Predicate;

/**
 * Created by ameesh.trikha on 2/22/16.
 */
public interface QueryFilter extends Predicate<Node> {
}
