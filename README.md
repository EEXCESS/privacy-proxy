![EEXCESS](http://eexcess.eu/wp-content/uploads/2013/04/eexcess_Logo_neu1.jpg "EEXCESS")

## Purpose
The purpose of the privacy proxy is to ensure users privacy in the EEXCESS system. Each query issued by the clients (e.g., Google Chrome extension, Wordpress plug-in) is sent to the privacy proxy before it is forwarded to the federated recommender. It allows obfuscating or anonymising it when it is required by the user. For instance, if a user does not want to share her location, then the privacy proxy will ensure that this information does not get to the federated recommender. 

## Installation and Deployment
The privacy proxy is developed in Java. The easiest way to contribute to this project is to use a proper IDE (e.g., [Eclipse](http://eclipse.org/)). Dependencies are handled with Maven (no additional dependencies need to be installed manually). If you are using Eclipse, you can configure the project by doing: 
* Right-click on the project > Configure > Convert to Maven Project
* Right-click on the project > Maven > Update Project...
* Right-click on the project > Properties > Deployment Assembly > Add Maven dependencies (necessary if Tomcat runs in Eclipse). 

To deploy the privacy proxy, a WAR file must be created from the project. If you are using Eclipse, Run > Run As > Maven Build. 

## Getting started
At this stage of the project, the privacy proxy: 
* does not obfuscate queries, as we wanted to start when a stable and effective version of the federated recommender is available. Therefore, quries are simply forwarded to the federated recommender. 
* logs the queries. 

These functionalities are accessible throught services defined in PrivacyProxyService: 
```java
public class PrivacyProxyService {
  public Response responseJSON(@HeaderParam(Cst.TAG_ORIGIN) String origin, @Context HttpServletRequest req, @Context HttpServletResponse servletResp, String input) { ... }
  public Response log(@PathParam("InteractionType") String interactionType, @HeaderParam(Cst.TAG_ORIGIN) String origin, @Context HttpServletRequest req, @Context HttpServletResponse servletResp, String input) { ... }
  public Response logDisambiguate(@Context HttpServletResponse servletResp, String input) {
  public Response getRegisteredPartners(@Context HttpServletResponse servletResp) { ... }
}
```

The JAX-WS (Java API for XML Web Services) API is used to create web services. The official documentation is available [here](https://jax-ws.java.net). 
