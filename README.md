![EEXCESS](http://eexcess.eu/wp-content/uploads/2013/04/eexcess_Logo_neu1.jpg "EEXCESS")

## Purpose
The purpose of the privacy proxy is to ensure users privacy in the EEXCESS system. Each query issued by the clients (e.g., Google Chrome extension, Wordpress plug-in) is sent to the privacy proxy before it is forwarded to the federated recommender. It provides anymisation techniques. For instance, if a user does not want to share her location, then the privacy proxy will ensure that this information does not get to the federated recommender. 

## Installation and Deployment

The Privacy Proxy is composed of two components: a requester and an issuer. Thus, this repository contains two Java projects. 

The easiest way to contribute to a project is to use a proper IDE (e.g., Eclipse). Dependencies are handled with Maven (no additional dependencies need to be installed manually). If you are using Eclipse, you can configure the project by doing: 
* Right-click on the project > Configure > Convert to Maven Project
* Right-click on the project > Maven > Update Project...
* Right-click on the project > Properties > Deployment Assembly > Add Maven dependencies (necessary if Tomcat runs in Eclipse). 

To deploy the privacy proxy, a WAR file must be created from each project. If you are using Eclipse, Run > Run As > Maven install. Each WAR must then be deployed on Tomcat servers and properly configured. 

## Getting started

The services offered by the Privacy Proxy are described in the [documentation](https://github.com/EEXCESS/eexcess/wiki/The-Privacy-Proxy-Services). 