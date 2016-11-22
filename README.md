AEM SQL2 Search
========

Applies to AEM 6.1

AEM 6.1 deprecates the XPATH querying. Querybuilder interface translates the predicates for search to XPATH Query 
and then AEM 6.1's underlying engine translates that XPATH query to SQL2 query.
This has multiple side effects that we have noticed during the upgrade to AEM 6.1 from 5.6. 
The translated queries adds additional properties to selection which causes the mismatch in the index created to support
the optimal performance of the query. Thus we added this module to support SQL2 based querying on AEM.

**Design principle** is simple - Each functionality that requires search to be performed is responsible for manging its query creation, this way each functionality can build the query using its own parameters. Given that search parameters can be component configured or request based. On top of it the pagenation and filerting requirement are very specific to components so its more desirable that the query generation logic is delegated to components themselves and the common logic of querying and providing massaged result is made common/centric.

To achieve this we have provided ***QueryProvider*** interface, a component must implement this class in order to plug into the Search setup. From reusability standpoint common functionaly for the Query providers has been abstracted to ***AbstractQueryProvider***
 class for building up the simple SQL2 queries. The code contains a sample implementation of Full Text Search implemented in ***FullTextSearchQueryProvider*** class that extends ***AbstractQueryProvider***. Only responsiblity of the ***FullTextSearchQueryProvider*** is extract the parameters/properties required to support search and provide it to base class for query generation.

These providers are plugged into the system using Adapter Pattern allowing the SlingHttpServletRequest to be adapted to these providers. Sample implementation in ***SqlQueryAdapterFactory*** class that adapts sling request to ***FullTextSearchQueryProvider***.

The search is facilitated by osgi service class ***Sql2Search*** which takes in the Provider implementation and Sling request to adapt it Provider class to get the SQLQuery object which provides the search result.

The code leverages the AEM's search predicates, predicate evalutators, facet extractors to get the facets from the search result.

**Facets** are configured separately for every provider class and this done using the factory configuration concept of OSGI. ***QueryFacetConfigurationFactory*** allows the configure the facets for each provider class and these configurations are exposed via a separate helper service ***QueryFacetPredicateProvider*** which manges the configuration binding by the provider name and exposes methods that allows to get those configuration for the named provider.
Building
--------

####Dependency - Must build aem-parent first and then aem-reusable-mods as parent pom provides the dependencies and configurations for the aem-reusable-mods.


This project uses Maven for building. Common commands:

From the root directory, run ``mvn -PautoInstallPackage clean install`` to build the bundle and content package and install to a CQ instance.

From the bundle directory, run ``mvn -PautoInstallBundle clean install`` to build *just* the bundle and install to a CQ instance.

Using with VLT
--------------

To use vlt with this project, first build and install the package to your local CQ instance as described above. Then cd to `content/src/main/content/jcr_root` and run

    vlt --credentials admin:admin checkout -f ../META-INF/vault/filter.xml --force http://localhost:4502/crx

Once the working copy is created, you can use the normal ``vlt up`` and ``vlt ci`` commands.

Specifying CRX Host/Port
------------------------

The CRX host and port can be specified on the command line with:
mvn -Dcrx.host=otherhost -Dcrx.port=5502 <goals>